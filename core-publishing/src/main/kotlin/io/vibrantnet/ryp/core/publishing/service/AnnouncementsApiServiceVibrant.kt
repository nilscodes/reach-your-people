package io.vibrantnet.ryp.core.publishing.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.publishing.CorePublishingConfiguration
import io.vibrantnet.ryp.core.publishing.model.*
import io.vibrantnet.ryp.core.publishing.persistence.Announcement
import io.vibrantnet.ryp.core.publishing.persistence.AnnouncementsRepository
import io.vibrantnet.ryp.core.publishing.persistence.AnnouncementsUpdateService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

val logger = KotlinLogging.logger {}

@Service
class AnnouncementsApiServiceVibrant(
    private val subscriptionService: SubscriptionService,
    private val verifyService: VerifyService,
    private val rabbitTemplate: RabbitTemplate,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper,
    private val announcementsRepository: AnnouncementsRepository,
    private val announcementUpdateService: AnnouncementsUpdateService,
    private val redirectService: RedirectService,
    private val config: CorePublishingConfiguration,
) : AnnouncementsApiService {

    override fun publishAnnouncementForProject(
        projectId: Long,
        announcement: BasicAnnouncementDto
    ): Mono<AnnouncementDto> {
        val linkedAccountsForAuthor = subscriptionService.getLinkedExternalAccounts(announcement.author)
        val announcementId = UUID.randomUUID()
        return redirectService.createShortUrlWithFallback("announcements/${announcementId}")
            .flatMap { shortLink ->
                val announcementWithId = announcement.toBasicAnnouncementWithIdDto(announcementId, shortLink)
                val persistedAnnouncement = createAnnouncement(announcementWithId, projectId)
                persistedAnnouncement.flatMap { persistedAnn ->
                    subscriptionService.getProject(projectId)
                        .flatMap { project ->
                            val policiesToPublishTo = project.policies.filter {
                                announcement.policies?.contains(it.policyId) ?: false
                            }
                            // For each policy to publish to, check if any of them are manually verified or CIP-66 and verify the user's right to publish announcements
                            Flux.fromIterable(policiesToPublishTo)
                                .concatMap { policy ->
                                    getAllVerificationStatusForAllLinkedAccounts(linkedAccountsForAuthor, policy)
                                }
                                .collectList()
                                .flatMap { verified ->
                                    if (verified.isNotEmpty() && verified.all { it.isPublishingAllowed() }) {
                                        checkVerificationStatusAndPublish(announcementWithId, projectId)
                                    } else {
                                        Mono.error(UserNotAuthorizedToPublishException("User with account ID ${announcement.author} is not authorized to publish announcements for project $projectId to policies ${policiesToPublishTo.joinToString(", ") { it.policyId }}."))
                                    }
                                }
                        }.thenReturn(persistedAnn)
                }.map {
                    AnnouncementDto(
                        announcementWithId.id,
                        it.projectId,
                        it.announcement,
                        it.status,
                        it.shortLink,
                        it.audience,
                        it.statistics,
                        it.createdDate,
                        it.modifiedDate,
                    )
                }
            }
    }

    override fun getPublishingPermissionsForAccount(projectId: Long, accountId: Long): Mono<PublishingPermissionsDto> {
        val linkedAccountsForAuthor = subscriptionService.getLinkedExternalAccounts(accountId)
        return subscriptionService.getProject(projectId)
            .flatMap { project ->
                Flux.fromIterable(project.policies)
                    .flatMap { policy ->
                        getAllVerificationStatusForAllLinkedAccounts(linkedAccountsForAuthor, policy)
                            .map { verified ->
                                PolicyPublishingPermissionDto(
                                    policy.policyId,
                                    verified
                                )
                            }
                    }.collectList()
                    .map {
                        PublishingPermissionsDto(it, accountId)
                    }
            }
    }

    override fun createAnnouncement(
        announcement: BasicAnnouncementWithIdDto,
        projectId: Long
    ): Mono<Announcement> {
        val announcementToStore = announcementFromBasicAnnouncement(announcement, projectId, config)
        return announcementsRepository.save(announcementToStore)
    }

    private fun getAllVerificationStatusForAllLinkedAccounts(
        linkedAccountsForAuthor: Flux<LinkedExternalAccountDto>,
        policy: PolicyDto,
    ): Mono<PublishingPermissionStatus> {
        return if (policy.manuallyVerified != null) {
            Mono.just(PublishingPermissionStatus.PUBLISHING_MANUAL)
        } else {
            linkedAccountsForAuthor.flatMap { linkedAccount ->
                verifyService.verifyCip66(
                    policy.policyId,
                    linkedAccount.externalAccount.type,
                    linkedAccount.externalAccount.referenceId
                )
            }
                .any { it }
                .map { verified ->
                    if (verified) {
                        PublishingPermissionStatus.PUBLISHING_CIP66
                    } else {
                        PublishingPermissionStatus.PUBLISHING_NOT_GRANTED
                    }
                }
        }
    }

    private fun checkVerificationStatusAndPublish(
        announcement: BasicAnnouncementWithIdDto,
        projectId: Long,
    ): Mono<Unit> {
        val announcementJob = AnnouncementJobDto(
            projectId,
            announcement.id,
        )

        logger.info { "Publishing announcement ${announcement.id} for project $projectId" }
        redisTemplate.opsForValue()
            .set("announcementsdata:${announcement.id}", announcement, 48, java.util.concurrent.TimeUnit.HOURS)
        rabbitTemplate.convertAndSend("announcements", announcementJob)
        return Mono.empty()
    }

    @RabbitListener(queues = ["completed"])
    fun sendAnnouncementToSubscribers(announcementJob: AnnouncementJobDto) {
        val minimalProjectInfo = getBasicProjectDto(announcementJob)
        announcementUpdateService.updateAnnouncementStatus(
            announcementJob.announcementId.toString(),
            AnnouncementStatus.PUBLISHING
        )
            .subscribe()
        val recipientsRaw = redisTemplate.opsForList().range("announcements:${announcementJob.announcementId}", 0, -1)
        val recipients =
            recipientsRaw?.map { objectMapper.convertValue(it, AnnouncementRecipientDto::class.java) } ?: emptyList()
        logger.info { "Sending announcement ${announcementJob.announcementId} for project ${announcementJob.projectId} to ${recipients.size} subscribers" }
        try {
            val announcementRaw = redisTemplate.opsForValue()["announcementsdata:${announcementJob.announcementId}"]
            val announcement = objectMapper.convertValue(announcementRaw, BasicAnnouncementWithIdDto::class.java)
            recipients.forEach { recipient ->
                rabbitTemplate.convertAndSend(
                    recipient.type, MessageDto(
                        recipient.referenceId,
                        announcement,
                        recipient.metadata,
                        minimalProjectInfo,
                    )
                )
            }
            redisTemplate.delete("announcements:${announcementJob.announcementId}")
            redisTemplate.delete("announcementsdata:${announcementJob.announcementId}")

            announcementUpdateService.updateAnnouncementStatistics(
                announcementJob.announcementId.toString(),
                calculateInitialStatistics(recipients),
                AnnouncementStatus.PUBLISHED,
            )
                .subscribe()
        } catch (e: Exception) {
            logger.error(e) { "Failed to send announcement ${announcementJob.announcementId} for project ${announcementJob.projectId}" }
            announcementUpdateService.updateAnnouncementStatus(
                announcementJob.announcementId.toString(),
                AnnouncementStatus.FAILED
            )
                .subscribe()
        }
    }

    private fun calculateInitialStatistics(recipients: List<AnnouncementRecipientDto>) = Statistics(
        sent = recipients
            .groupBy { it.type }
            .mapValues { it.value.size.toLong() },
        uniqueAccounts = recipients
            .distinctBy { it.accountId }.size.toLong(),
        explicitSubscribers = recipients
            .filter { it.subscriptionStatus == SubscriptionStatus.SUBSCRIBED }
            .distinctBy { it.accountId }.size.toLong(),
    )

    private fun getBasicProjectDto(announcementJob: AnnouncementJobDto): BasicProjectDto {
        val project = subscriptionService.getProject(announcementJob.projectId)
            .blockOptional()
            .orElseThrow { IllegalStateException("Cannot publish announcement ${announcementJob.announcementId} for project ${announcementJob.projectId} as the project does not exist.") }
        return BasicProjectDto(project)
    }

    override fun listAnnouncementsForProject(projectId: Long): Flux<AnnouncementDto> {
        return announcementsRepository.findByProjectId(projectId)
            .map { it.toDto() }
    }

    override fun getAnnouncementById(announcementId: UUID): Mono<AnnouncementDto> {
        return announcementsRepository.findById(announcementId.toString())
            .map { it.toDto() }
            .switchIfEmpty(Mono.error(NoSuchElementException("Announcement with ID $announcementId not found.")))
    }
}

fun announcementFromBasicAnnouncement(
    announcement: BasicAnnouncementWithIdDto,
    projectId: Long,
    config: CorePublishingConfiguration,
) = Announcement(
    announcement.id.toString(),
    projectId,
    ActivityStream(
        id = "${config.baseUrl}/announcements/${announcement.id}",
        actor = Person(
            name = announcement.author.toString(),
            id = "${config.baseUrl}/users/${announcement.author}"
        ),
        `object` = Note(
            content = announcement.content,
            summary = announcement.title,
            url = announcement.externalLink
        ),
        attributedTo = Organization(
            name = "RYP Project $projectId",
            id = "${config.baseUrl}/projects/$projectId"
        )
    ),
    AnnouncementStatus.PENDING,
    announcement.link,
    Audience(
        policies = announcement.policies ?: emptyList()
    )
)
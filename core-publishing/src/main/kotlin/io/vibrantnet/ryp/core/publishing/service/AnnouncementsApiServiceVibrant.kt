package io.vibrantnet.ryp.core.publishing.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.publishing.model.*
import io.vibrantnet.ryp.core.publishing.persistence.Announcement
import io.vibrantnet.ryp.core.publishing.persistence.AnnouncementsRepository
import io.vibrantnet.ryp.core.publishing.persistence.AnnouncementsUpdateService
import mu.KotlinLogging
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
    val subscriptionService: SubscriptionService,
    val verifyService: VerifyService,
    val rabbitTemplate: RabbitTemplate,
    val redisTemplate: RedisTemplate<String, Any>,
    val objectMapper: ObjectMapper,
    val announcementsRepository: AnnouncementsRepository,
    val announcementUpdateService: AnnouncementsUpdateService,
) : AnnouncementsApiService {

    override fun publishAnnouncementForProject(
        projectId: Long,
        announcement: BasicAnnouncementDto
    ): Mono<AnnouncementDto> {
        val linkedAccountsForAuthor = subscriptionService.getLinkedExternalAccounts(announcement.author)
        val announcementId = UUID.randomUUID()
        val persistedAnnouncement = createAnnouncement(announcement, announcementId, projectId)
        return persistedAnnouncement.doOnSuccess {
            subscriptionService.getProject(projectId)
                .flatMap { project ->
                    // For each policy in the project, check if any of them are CIP-66 and verify the user's right to publish announcements
                    Flux.fromIterable(project.policies)
                        .flatMap { policy ->
                            getAllVerificationStatusForAllLinkedAccounts(linkedAccountsForAuthor, policy)
                        }
                        .any { it } // If any of the verifications succeeded
                        .flatMap { verified ->
                            checkVerificationStatusAndPublish(verified, announcementId, announcement, projectId)
                        }
                }.subscribe()
        }.map { AnnouncementDto(announcementId, it.projectId, it.announcement, it.status) }
    }

    private fun createAnnouncement(
        announcement: BasicAnnouncementDto,
        announcementId: UUID,
        projectId: Long
    ): Mono<Announcement> {
        val announcementToStore = Announcement(
            announcementId.toString(),
            projectId,
            ActivityStream(
                id = "https://ryp.vibrantnet.io/announcements/${announcementId}",
                actor = Person(
                    name = announcement.author.toString(),
                    url = "https://ryp.vibrantnet.io/users/${announcement.author}"
                ),
                `object` = Note(
                    content = announcement.content
                )
            ),
            AnnouncementStatus.PENDING,
        )

        return announcementsRepository.save(announcementToStore)
    }


    private fun getAllVerificationStatusForAllLinkedAccounts(
        linkedAccountsForAuthor: Flux<LinkedExternalAccountDto>,
        policy: PolicyDto,
    ): Flux<Boolean> = linkedAccountsForAuthor.flatMap { linkedAccount ->
        verifyService.verifyCip66(
            policy.policyId,
            linkedAccount.externalAccount.type,
            linkedAccount.externalAccount.referenceId
        )
    }

    private fun checkVerificationStatusAndPublish(
        verified: Boolean,
        announcementId: UUID,
        announcement: BasicAnnouncementDto,
        projectId: Long,
    ): Mono<Unit> = if (!verified) {
        Mono.error(UserNotAuthorizedToPublishException("User with account ID ${announcement.author} is not authorized to publish announcements for project $projectId"))
    } else {
        val announcementJob = AnnouncementJobDto(
            projectId,
            announcementId,
        )

        logger.info { "Publishing announcement ${announcementId} for project $projectId" }
        redisTemplate.opsForValue().set("announcementsdata:${announcementId}", announcement, 48, java.util.concurrent.TimeUnit.HOURS)
        rabbitTemplate.convertAndSend("announcements", announcementJob)
        Mono.empty()
    }

    @RabbitListener(queues = ["completed"])
    fun sendAnnouncementToSubscribers(announcementJob: AnnouncementJobDto) {
        announcementUpdateService.updateAnnouncementStatus(announcementJob.announcementId.toString(), AnnouncementStatus.PUBLISHING)
            .subscribe()
        val recipientsRaw = redisTemplate.opsForList().range("announcements:${announcementJob.announcementId}", 0, -1)
        val recipients = recipientsRaw?.map { objectMapper.convertValue(it, AnnouncementRecipientDto::class.java) } ?: emptyList()
        logger.info { "Sending announcement ${announcementJob.announcementId} for project ${announcementJob.projectId} to ${recipients.size} subscribers" }
        val announcementRaw = redisTemplate.opsForValue()["announcementsdata:${announcementJob.announcementId}"]
        val announcement = objectMapper.convertValue(announcementRaw, BasicAnnouncementDto::class.java)
        recipients.forEach { recipient ->
            rabbitTemplate.convertAndSend(recipient.type, MessageDto(
                recipient.referenceId,
                announcement
            ))
         }
        redisTemplate.delete("announcements:${announcementJob.announcementId}")
        redisTemplate.delete("announcementsdata:${announcementJob.announcementId}")
        announcementUpdateService.updateAnnouncementStatus(announcementJob.announcementId.toString(), AnnouncementStatus.PUBLISHED)
            .subscribe()
    }
}
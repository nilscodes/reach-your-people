package io.vibrantnet.ryp.core.subscription.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.ryp.shared.model.AnnouncementRecipientDto
import io.ryp.shared.model.AnnouncementJobDto
import io.ryp.shared.model.SnapshotRequestDto
import io.ryp.shared.model.TokenOwnershipInfoWithAssetCount
import io.vibrantnet.ryp.core.subscription.model.SubscriptionStatus
import io.vibrantnet.ryp.core.subscription.persistence.AccountRepository
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccount
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountCustomRepository
import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

val logger = KotlinLogging.logger {}

@Service
class SubscriptionServiceVibrant(
    val accountRepository: AccountRepository,
    val projectsService: ProjectsApiService,
    val externalAccountCustomRepository: ExternalAccountCustomRepository,
    val redisTemplate: RedisTemplate<String, Any>,
    val rabbitTemplate: RabbitTemplate,
    val objectMapper: ObjectMapper,
) {

    @RabbitListener(queues = ["announcements"])
    @Transactional
    fun prepareRecipients(announcementJob: AnnouncementJobDto) {
        logger.info { "Preparing recipients for announcement ${announcementJob.announcementId} for project ${announcementJob.projectId}" }
        projectsService.getProject(announcementJob.projectId).blockOptional().ifPresent { project ->
            if (project.policies.isEmpty()) {
                logger.info { "No policies found for project ${announcementJob.announcementId}, publishing immediately to direct subscribers." }
                val recipients = getExplicitlySubscribedAccounts(announcementJob.projectId)
                redisTemplate.opsForList().rightPushAll("announcements:${announcementJob.announcementId}", recipients)
                redisTemplate.expire("announcements:${announcementJob.announcementId}", 48, java.util.concurrent.TimeUnit.HOURS)
                rabbitTemplate.convertAndSend("completed", announcementJob.announcementId)
            } else {
                logger.info { "Policies found for project ${announcementJob.announcementId}, sending snapshot request for ${project.policies.size} policies." }
                val policyIds = project.policies.map { it.policyId }
                rabbitTemplate.convertAndSend("snapshot", SnapshotRequestDto(announcementJob, policyIds))
            }
        }
    }

    private fun getExplicitlySubscribedAccounts(projectId: Long): List<AnnouncementRecipientDto> {
        val recipients = accountRepository.findExternalAccountsByProjectIdAndSubscriptionStatus(
            projectId = projectId,
            status = SubscriptionStatus.SUBSCRIBED
        ).map {
            announcementRecipientDtoFromExternalAccount(it)
        }
        return recipients
    }

    private fun announcementRecipientDtoFromExternalAccount(it: ExternalAccount) =
        AnnouncementRecipientDto(
            type = it.type,
            referenceId = it.referenceId,
            metadata = it.metadata.let { metadata ->
                if (metadata != null) {
                    Base64.getEncoder().encodeToString(metadata)
                } else {
                    null
                }
            },
        )

    @RabbitListener(queues = ["snapshotcompleted"])
    fun processSnapshotCompleted(announcementJob: AnnouncementJobDto) {
        logger.info { "Processing snapshot completion for announcement ${announcementJob.announcementId}, snapshot ID is ${announcementJob.snapshotId}" }
        val snapshotDataRaw = redisTemplate.opsForList().range("snapshot:${announcementJob.snapshotId}", 0, -1) ?: emptyList()
        val snapshotData = snapshotDataRaw.map {
            objectMapper.convertValue(it, TokenOwnershipInfoWithAssetCount::class.java)
        }
        val externalAccounts = externalAccountCustomRepository.findEligibleExternalAccounts(1, snapshotData.map { it.stakeAddress })
        val recipients = externalAccounts.map {
            announcementRecipientDtoFromExternalAccount(it)
        }.toMutableSet()
        recipients.addAll(getExplicitlySubscribedAccounts(announcementJob.projectId))
        if (recipients.isEmpty()) {
            logger.info { "No recipients found for announcement ${announcementJob.announcementId}, skipping sending." }
            // TODO ensure the job is cancelled or marked done and recipient count is tracked
            return
        }
        redisTemplate.opsForList().rightPushAll("announcements:${announcementJob.announcementId}", recipients.toList())
        redisTemplate.expire("announcements:${announcementJob.announcementId}", 48, java.util.concurrent.TimeUnit.HOURS)
        rabbitTemplate.convertAndSend("completed", announcementJob)
    }
}
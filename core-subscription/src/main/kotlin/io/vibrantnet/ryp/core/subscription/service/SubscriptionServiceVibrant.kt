package io.vibrantnet.ryp.core.subscription.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.subscription.persistence.AccountRepository
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountRepository
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountWithAccountProjection
import jakarta.transaction.Transactional
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
    val externalAccountRepository: ExternalAccountRepository,
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
        val recipients = externalAccountRepository.findExternalAccountsByProjectIdAndSubscriptionStatus(
            projectId = projectId,
            status = SubscriptionStatus.SUBSCRIBED
        ).map {
            announcementRecipientDtoFromExternalAccount(it, SubscriptionStatus.SUBSCRIBED)
        }
        return recipients
    }

    private fun announcementRecipientDtoFromExternalAccount(it: ExternalAccountWithAccountProjection, subscriptionStatus: SubscriptionStatus) =
        AnnouncementRecipientDto(
            externalAccountId = it.id,
            type = it.type,
            accountId = it.accountId,
            referenceId = it.referenceId,
            metadata = it.metadata.let { metadata ->
                if (metadata != null) {
                    Base64.getEncoder().encodeToString(metadata)
                } else {
                    null
                }
            },
            subscriptionStatus = subscriptionStatus,
        )

    @RabbitListener(queues = ["snapshotcompleted"])
    fun processSnapshotCompleted(announcementJob: AnnouncementJobDto) {
        logger.info { "Processing snapshot completion for announcement ${announcementJob.announcementId}, snapshot ID is ${announcementJob.snapshotId}" }
        val snapshotDataRaw = redisTemplate.opsForList().range("snapshot:${announcementJob.snapshotId}", 0, -1) ?: emptyList()
        val snapshotData = snapshotDataRaw.map {
            objectMapper.convertValue(it, TokenOwnershipInfoWithAssetCount::class.java)
        }
        val accountIds = externalAccountRepository.findEligibleAccountsByWallet(announcementJob.projectId, snapshotData.map { it.stakeAddress }, listOf(SubscriptionStatus.BLOCKED, SubscriptionStatus.MUTED))
        val externalAccounts = externalAccountRepository.findMessagingExternalAccountsForProjectAndAccounts(announcementJob.projectId, accountIds, listOf("cardano"))
        val recipients = externalAccounts.map {
            announcementRecipientDtoFromExternalAccount(it, SubscriptionStatus.DEFAULT)
        }.toMutableSet()
        val explicitlySubscribed = getExplicitlySubscribedAccounts(announcementJob.projectId)
        val onlyExplicitlySubscribed = explicitlySubscribed.filter { recipient ->
            recipients.none { it.externalAccountId == recipient.externalAccountId }
        }
        recipients.addAll(onlyExplicitlySubscribed)
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
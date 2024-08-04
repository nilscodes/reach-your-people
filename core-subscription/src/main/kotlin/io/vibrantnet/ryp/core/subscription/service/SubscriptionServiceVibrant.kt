package io.vibrantnet.ryp.core.subscription.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ryp.cardano.model.SnapshotRequestDto
import io.ryp.cardano.model.SnapshotStakeAddressDto
import io.ryp.cardano.model.SnapshotType
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountRepository
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountSettingsUtil
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
    private val projectsService: ProjectsApiService,
    private val externalAccountRepository: ExternalAccountRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper,
) {

    @RabbitListener(queues = ["announcements"])
    @Transactional
    fun prepareRecipients(announcementJob: AnnouncementJobDto) {
        logger.info { "Preparing recipients for announcement ${announcementJob.announcementId} for project ${announcementJob.projectId}" }
        projectsService.getProject(announcementJob.projectId).blockOptional().ifPresent {
            // TODO Decide if we want to check if policies/stakepools have been removed from the project in the meantime. Currently, we ignore the project info and just make sure it still exists
            val announcementRaw = redisTemplate.opsForValue()["announcementsdata:${announcementJob.announcementId}"]
            val announcement = objectMapper.convertValue(announcementRaw, BasicAnnouncementWithIdDto::class.java)
            val policiesToAnnounceTo = announcement.policies ?: emptyList()
            val stakepoolsToAnnounceTo = announcement.stakepools ?: emptyList()
            if (policiesToAnnounceTo.isEmpty() && stakepoolsToAnnounceTo.isEmpty()) {
                logger.info { "No policies or stakepools found for announcement ${announcementJob.announcementId}, publishing immediately and only to direct subscribers." }
                val recipients = getExplicitlySubscribedAccounts(announcementJob.projectId)
                redisTemplate.opsForList().rightPushAll("announcements:${announcementJob.announcementId}", recipients)
                redisTemplate.expire("announcements:${announcementJob.announcementId}", 48, java.util.concurrent.TimeUnit.HOURS)
                rabbitTemplate.convertAndSend("completed", announcementJob.announcementId)
            } else {
                logger.info { "Policies and stakepools found for announcement ${announcementJob.announcementId}, sending snapshot request for ${policiesToAnnounceTo.size} policies and ${stakepoolsToAnnounceTo.size} stakepools." }
                rabbitTemplate.convertAndSend("snapshot", SnapshotRequestDto(announcementJob, policiesToAnnounceTo, stakepoolsToAnnounceTo))
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
            referenceName = it.referenceName,
        )

    @RabbitListener(queues = ["snapshotcompleted"])
    fun processSnapshotCompleted(announcementJob: AnnouncementJobDto) {
        logger.info { "Processing snapshot completion for announcement ${announcementJob.announcementId}, snapshot ID is ${announcementJob.snapshotId}" }
        val snapshotDataRaw = redisTemplate.opsForList().range("snapshot:${announcementJob.snapshotId}", 0, -1) ?: emptyList()
        val snapshotData = snapshotDataRaw.map {
            objectMapper.convertValue(it, SnapshotStakeAddressDto::class.java)
        }
        val accountIds = collectAutomaticallySubscribedAccounts(snapshotData, announcementJob)
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

    private fun collectAutomaticallySubscribedAccounts(
        snapshotData: List<SnapshotStakeAddressDto>,
        announcementJob: AnnouncementJobDto
    ): List<Int> {
        val tokenWallets = snapshotData.filter { it.snapshotType == SnapshotType.POLICY }.map { it.stakeAddress }
        val tokenAccountIds = externalAccountRepository.findEligibleAccountsByWallet(
            announcementJob.projectId,
            tokenWallets,
            listOf(SubscriptionStatus.BLOCKED, SubscriptionStatus.MUTED),
            ExternalAccountSettingsUtil.settingsFromSet(setOf(ExternalAccountSetting.NON_FUNGIBLE_TOKEN_ANNOUNCEMENTS, ExternalAccountSetting.FUNGIBLE_TOKEN_ANNOUNCEMENTS, ExternalAccountSetting.RICH_FUNGIBLE_TOKEN_ANNOUNCEMENTS), '0')
        )
        val stakepoolWallets = snapshotData.filter { it.snapshotType == SnapshotType.STAKEPOOL }.map { it.stakeAddress }
        val stakepoolAccountIds = externalAccountRepository.findEligibleAccountsByWallet(
            announcementJob.projectId,
            stakepoolWallets,
            listOf(SubscriptionStatus.BLOCKED, SubscriptionStatus.MUTED),
            ExternalAccountSettingsUtil.settingsFromSet(setOf(ExternalAccountSetting.STAKEPOOL_ANNOUNCEMENTS), '0')
        )
        return tokenAccountIds + stakepoolAccountIds
    }
}
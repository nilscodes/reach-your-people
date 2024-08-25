package io.vibrantnet.ryp.core.subscription.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ryp.cardano.model.SnapshotRequestDto
import io.ryp.cardano.model.SnapshotStakeAddressDto
import io.ryp.cardano.model.SnapshotType
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.subscription.model.CardanoSetting
import io.vibrantnet.ryp.core.subscription.persistence.CardanoSettingsUtil
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountRepository
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountSettingsUtil
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountWithAccountProjection
import jakarta.transaction.Transactional
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class SubscriptionServiceVibrant(
    private val externalAccountRepository: ExternalAccountRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper,
) {

    @RabbitListener(queues = ["announcements"])
    @Transactional
    fun prepareRecipients(announcementJob: AnnouncementJobDto) {
        logger.info { "Preparing recipients for announcement ${announcementJob.announcementId} for project ${announcementJob.projectId}" }
        val announcementRaw = redisTemplate.opsForValue()["announcementsdata:${announcementJob.announcementId}"]
        val announcement = objectMapper.convertValue(announcementRaw, BasicAnnouncementWithIdDto::class.java)
        logger.debug { "Announcement type for ${announcementJob.announcementId} is ${announcement.type} " }
        val policiesToAnnounceTo = announcement.policies ?: emptyList()
        val stakepoolsToAnnounceTo = announcement.stakepools ?: emptyList()
        val dRepsToAnnounceTo = announcement.dreps ?: emptyList()
        // TODO if announcement type is event-based, we might determine to look up explicit subscribers by looking up for example the SPO/dRep by ID? Could also be considered a responsibility of the event service
        if (policiesToAnnounceTo.isEmpty() && stakepoolsToAnnounceTo.isEmpty() && dRepsToAnnounceTo.isEmpty()) {
            logger.info { "No policies or stakepools or dReps found for announcement ${announcementJob.announcementId}, publishing without snapshot and only to direct subscribers of project ${announcementJob.projectId} or global audience ${announcement.global}." }
            val recipients = getExplicitlySubscribedAccounts(announcementJob.projectId, announcementJob.global)
            if (recipients.isNotEmpty()) {
                redisTemplate.opsForList().rightPushAll("announcements:${announcementJob.announcementId}", recipients)
                redisTemplate.expire(
                    "announcements:${announcementJob.announcementId}",
                    48,
                    java.util.concurrent.TimeUnit.HOURS
                )
                rabbitTemplate.convertAndSend("completed", announcementJob)
            }
        } else {
            logger.info { "Policies or stakepools or dReps found for announcement ${announcementJob.announcementId}, sending snapshot request for ${policiesToAnnounceTo.size} policies and ${stakepoolsToAnnounceTo.size} stakepools and ${dRepsToAnnounceTo.size} dReps." }
            rabbitTemplate.convertAndSend("snapshot", SnapshotRequestDto(announcementJob, policiesToAnnounceTo, stakepoolsToAnnounceTo, dRepsToAnnounceTo))
        }
    }

    private fun getExplicitlySubscribedAccounts(
        projectId: Long,
        announcementAudiences: List<GlobalAnnouncementAudience>
    ) = if (projectId > 0) {
        getExplicitlySubscribedAccountsForProject(projectId)
    } else {
        getGloballySubscribedAccountsForAnnouncementType(announcementAudiences)
    }

    private fun getExplicitlySubscribedAccountsForProject(projectId: Long): List<AnnouncementRecipientDto> {
        return if (projectId > 0) {
            externalAccountRepository.findExternalAccountsByProjectIdAndSubscriptionStatus(
                projectId = projectId,
                status = SubscriptionStatus.SUBSCRIBED
            ).map {
                announcementRecipientDtoFromExternalAccount(it, SubscriptionStatus.SUBSCRIBED)
            }
        } else {
            emptyList()
        }
    }

    private fun getGloballySubscribedAccountsForAnnouncementType(announcementAudiences: List<GlobalAnnouncementAudience>): List<AnnouncementRecipientDto> {
        return announcementAudiences.flatMap { audienceType ->
            val settingsForAudienceType = getSettingsForAudienceType(audienceType)
            externalAccountRepository.findExternalAccountsForGlobalAudience(ExternalAccountRole.OWNER.ordinal, settingsForAudienceType).map {
                announcementRecipientDtoFromExternalAccount(it, SubscriptionStatus.SUBSCRIBED)
            }
        }
    }

    private fun getSettingsForAudienceType(audienceType: GlobalAnnouncementAudience) =
        when(audienceType) {
            GlobalAnnouncementAudience.GOVERNANCE_CARDANO -> CardanoSettingsUtil.settingsFromSet(setOf(CardanoSetting.GOVERNANCE_ACTION_ANNOUNCEMENTS))
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
        val explicitlySubscribed = getExplicitlySubscribedAccounts(announcementJob.projectId, announcementJob.global)
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
        val dRepWallets = snapshotData.filter { it.snapshotType == SnapshotType.DREP }.map { it.stakeAddress }
        val dRepAccountIds = externalAccountRepository.findEligibleAccountsByWallet(
            announcementJob.projectId,
            dRepWallets,
            listOf(SubscriptionStatus.BLOCKED, SubscriptionStatus.MUTED),
            ExternalAccountSettingsUtil.settingsFromSet(setOf(ExternalAccountSetting.DREP_ANNOUNCEMENTS), '0')
        )
        return tokenAccountIds + stakepoolAccountIds + dRepAccountIds
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
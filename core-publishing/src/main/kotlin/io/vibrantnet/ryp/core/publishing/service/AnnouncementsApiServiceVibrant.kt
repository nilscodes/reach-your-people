package io.vibrantnet.ryp.core.publishing.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.publishing.model.UserNotAuthorizedToPublishException
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AnnouncementsApiServiceVibrant(
    val subscriptionService: SubscriptionService,
    val verifyService: VerifyService,
    val rabbitTemplate: RabbitTemplate,
    val redisTemplate: RedisTemplate<String, Any>,
    val objectMapper: ObjectMapper,
) : AnnouncementsApiService {

    override fun publishAnnouncementForProject(
        projectId: Long,
        announcement: BasicAnnouncementDto
    ): Mono<Unit> {
        val linkedAccountsForAuthor = subscriptionService.getLinkedExternalAccounts(announcement.author)
        return subscriptionService.getProject(projectId)
            .flatMap { project ->
                // For each policy in the project, check if any of them are CIP-66 and verify the user's right to publish announcements
                Flux.fromIterable(project.policies)
                    .flatMap { policy ->
                        getAllVerificationStatusForAllLinkedAccounts(linkedAccountsForAuthor, policy)
                    }
                    .any { it } // If any of the verifications succeeded
                    .flatMap { verified ->
                        checkVerificationStatusAndPublish(verified, announcement, projectId)
                    }
            }
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
        announcement: BasicAnnouncementDto,
        projectId: Long,
    ): Mono<Unit> = if (!verified) {
        Mono.error(UserNotAuthorizedToPublishException("User with account ID ${announcement.author} is not authorized to publish announcements for project $projectId"))
    } else {
        redisTemplate.opsForValue()["announcementsdata:$projectId"] = announcement
        redisTemplate.expire("announcementsdata:$projectId", 48, java.util.concurrent.TimeUnit.HOURS)
        rabbitTemplate.convertAndSend("announcements", projectId)
        Mono.empty()
    }

    @RabbitListener(queues = ["completed"])
    fun sendAnnouncementToSubscribers(projectId: Long) {
        val recipientsRaw = redisTemplate.opsForList().range("announcements:$projectId", 0, -1)
        val recipients = recipientsRaw?.map { objectMapper.convertValue(it, AnnouncementRecipientDto::class.java) } ?: emptyList()
        val announcementRaw = redisTemplate.opsForValue()["announcementsdata:$projectId"]
        val announcement = objectMapper.convertValue(announcementRaw, BasicAnnouncementDto::class.java)
        recipients.forEach { recipient ->
            rabbitTemplate.convertAndSend(recipient.type, MessageDto(
                recipient.referenceId,
                announcement
            ))
         }
        redisTemplate.delete("announcements:$projectId")
        redisTemplate.delete("announcementsdata:$projectId")
    }
}
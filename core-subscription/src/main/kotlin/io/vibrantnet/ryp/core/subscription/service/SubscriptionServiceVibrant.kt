package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.model.AnnouncementRecipientDto
import io.vibrantnet.ryp.core.subscription.model.SubscriptionStatus
import io.vibrantnet.ryp.core.subscription.persistence.AccountRepository
import jakarta.transaction.Transactional
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class SubscriptionServiceVibrant(
    val accountRepository: AccountRepository,
    val projectsService: ProjectsApiService,
    val redisTemplate: RedisTemplate<String, Any>,
    val rabbitTemplate: RabbitTemplate,
) {

    @RabbitListener(queues = ["announcements"])
    @Transactional
    fun prepareRecipients(projectId: Long) {
        projectsService.getProject(projectId).blockOptional().ifPresent { project ->
            // TODO get wallet snapshot and merge it with subscriptions and blocks
            val recipients = accountRepository.findExternalAccountsByProjectIdAndSubscriptionStatus(
                projectId = projectId,
                status = SubscriptionStatus.SUBSCRIBED
            ).map {
                AnnouncementRecipientDto(
                    type = it.type,
                    referenceId = it.referenceId
                )
            }
            // TODO should be a unique key with a uuid associated with the job
            redisTemplate.opsForList().rightPushAll("announcements:$projectId", recipients)
            rabbitTemplate.convertAndSend("completed", projectId)
        }
    }
}
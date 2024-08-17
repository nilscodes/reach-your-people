package io.vibrantnet.ryp.core.publishing.service

import io.ryp.cardano.model.EventNotification
import io.ryp.shared.model.AnnouncementJobDto
import io.ryp.shared.model.AnnouncementType
import io.ryp.shared.model.BasicAnnouncementWithIdDto
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class EventsServiceVibrant(
    private val redirectService: RedirectService,
    private val announcementsApiServiceVibrant: AnnouncementsApiServiceVibrant,
    private val rabbitTemplate: RabbitTemplate,
    private val redisTemplate: RedisTemplate<String, Any>,
) {

    @RabbitListener(queues = ["event-notifications"])
    fun receiveMessage(eventNotification: EventNotification) {
        println("Received message: $eventNotification")
        val announcementId = UUID.randomUUID()
        redirectService.createShortUrlWithFallback("announcements/${announcementId}")
            .flatMap { shortLink ->
                val announcementWithId = BasicAnnouncementWithIdDto(
                    id = UUID.randomUUID(),
                    type = AnnouncementType.fromEventType(eventNotification.type),
                    author = 0L,
                    title = "Governance activity (vote)",
                    content = "Your delegated representative voted **YES** on a treasury withdrawal proposal for 3,000,000 ADA with the title: *Bringing the Cardano Logo to Twitter hashtags*\n\n"
                            + "**Their submitted comment**\n"
                    + "${eventNotification.comment}\n\n"
                     + "**Link to transaction**\n<https://preview.cardanoscan.io/transaction/${eventNotification.transactionHash}>",
                    link = shortLink,
                    externalLink = "",
                    policies = eventNotification.audience.policies,
                    stakepools = eventNotification.audience.stakepools,
                    dreps = eventNotification.audience.dreps
                )
                announcementsApiServiceVibrant.createAnnouncement(announcementWithId, 0)
                    .map {
                        publishEventAnnouncement(announcementWithId)
                    }
            }.subscribe()
    }

    private fun publishEventAnnouncement(
        announcement: BasicAnnouncementWithIdDto,
    ): Mono<Unit> {
        val announcementJob = AnnouncementJobDto(
            0,
            announcement.id,
        )

        logger.info { "Publishing event notification ${announcement.id} for event, publishing to policies: ${announcement.policies}, stakepools: ${announcement.stakepools}, dReps: ${announcement.dreps}" }
        redisTemplate.opsForValue()
            .set("announcementsdata:${announcement.id}", announcement, 48, java.util.concurrent.TimeUnit.HOURS)
        rabbitTemplate.convertAndSend("announcements", announcementJob)
        return Mono.empty()
    }
}
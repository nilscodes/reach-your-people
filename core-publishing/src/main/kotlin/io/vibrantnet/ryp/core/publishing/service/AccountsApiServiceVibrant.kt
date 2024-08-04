package io.vibrantnet.ryp.core.publishing.service

import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.publishing.CorePublishingConfiguration
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class AccountsApiServiceVibrant(
    private val subscriptionService: SubscriptionService,
    private val rabbitTemplate: RabbitTemplate,
    private val config: CorePublishingConfiguration,
    private val announcementsApiService: AnnouncementsApiService,
) : AccountsApiService {
    override fun sendTestAnnouncement(accountId: Long, externalAccountId: Long): Mono<BasicAnnouncementWithIdDto> {
        return subscriptionService.getLinkedExternalAccounts(accountId)
            .filter { it.externalAccount.id == externalAccountId && it.role == ExternalAccountRole.OWNER }
            .next()
            .map {
                val announcementId = UUID.randomUUID()
                val announcement = BasicAnnouncementWithIdDto(
                    id = announcementId,
                    type = AnnouncementType.TEST,
                    author = accountId,
                    title = "Test Announcement",
                    content = "This is a test announcement",
                    link = config.baseUrl,
                    externalLink = null,
                    policies = null,
                )
                announcementsApiService.createAnnouncement(announcement, 0).subscribe()
                rabbitTemplate.convertAndSend(
                    it.externalAccount.type, MessageDto(
                        it.externalAccount.referenceId,
                        announcement,
                        it.externalAccount.metadata,
                        BasicProjectDto(
                            id = 0,
                            name = "Test Project",
                            logo = "",
                            url = announcement.link,
                        ),
                        it.externalAccount.referenceName,
                    )
                )
                announcement
            }
            .switchIfEmpty(Mono.error(IllegalArgumentException("Account $accountId does not have permission to send test announcements to external account $externalAccountId")))
    }
}
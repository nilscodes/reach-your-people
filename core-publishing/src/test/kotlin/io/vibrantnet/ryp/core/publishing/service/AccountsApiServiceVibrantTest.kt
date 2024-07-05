package io.vibrantnet.ryp.core.publishing.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.ryp.shared.model.AnnouncementType
import io.ryp.shared.model.ExternalAccountRole
import io.ryp.shared.model.MessageDto
import io.vibrantnet.ryp.core.publishing.CorePublishingConfiguration
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

private val defaultConfig = CorePublishingConfiguration(
    "", "", "", "https://ryp.io"
)

internal class AccountsApiServiceVibrantTest {

    @Test
    fun `sending a test announcement works if user is owner of the respective external account`() {
        val subscriptionService = mockk<SubscriptionService>()
        val announcementsApiService = mockk<AnnouncementsApiService>()
        val rabbitTemplate = mockk<RabbitTemplate>()
        val accountsApiService = AccountsApiServiceVibrant(subscriptionService, rabbitTemplate, defaultConfig, announcementsApiService)
        every { subscriptionService.getLinkedExternalAccounts(12) } answers {
            Flux.fromIterable(listOf(
                makeLinkedExternalAccountDto(12, 1212),
                makeLinkedExternalAccountDto(134, 5124, ExternalAccountRole.ADMIN),
            ))
        }
        every { announcementsApiService.createAnnouncement(any(), any()) } answers { Mono.empty() }
        every { rabbitTemplate.convertAndSend(any<String>(), any<MessageDto>()) } just Runs

        val result = accountsApiService.sendTestAnnouncement(12, 1212)

        StepVerifier.create(result)
            .assertNext {
                assertEquals(12, it.author)
                assertEquals(AnnouncementType.TEST, it.type)
                assertEquals(defaultConfig.baseUrl, it.link)
                assertEquals("This is a test announcement", it.content)
                assertEquals("Test Announcement", it.title)
            }
            .verifyComplete()
    }

    @Test
    fun `sending a test announcement fails if the user is not owner of the respective external account`() {
        val subscriptionService = mockk<SubscriptionService>()
        val announcementsApiService = mockk<AnnouncementsApiService>()
        val rabbitTemplate = mockk<RabbitTemplate>()
        val accountsApiService = AccountsApiServiceVibrant(subscriptionService, rabbitTemplate, defaultConfig, announcementsApiService)
        every { subscriptionService.getLinkedExternalAccounts(12) } answers {
            Flux.fromIterable(listOf(
                makeLinkedExternalAccountDto(12, 1212),
                makeLinkedExternalAccountDto(134, 5124, ExternalAccountRole.ADMIN),
            ))
        }
        every { announcementsApiService.createAnnouncement(any(), any()) } answers { Mono.empty() }
        every { rabbitTemplate.convertAndSend(any<String>(), any<MessageDto>()) } just Runs

        val result = accountsApiService.sendTestAnnouncement(12, 5124)

        StepVerifier.create(result)
            .expectError(IllegalArgumentException::class.java)
            .verify()
    }
}
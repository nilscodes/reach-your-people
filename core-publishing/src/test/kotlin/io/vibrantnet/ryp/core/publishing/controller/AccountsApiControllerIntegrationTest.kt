package io.vibrantnet.ryp.core.publishing.controller

import io.mockk.every
import io.mockk.mockk
import io.ryp.shared.model.AnnouncementType
import io.ryp.shared.model.BasicAnnouncementWithIdDto
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.publishing.service.AccountsApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.util.*

@WebFluxTest(controllers = [AccountsApiController::class, ApiExceptionHandler::class])
@ActiveProfiles("test")
internal class AccountsApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean fun accountsApiService() = mockk<AccountsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var accountsApiService: AccountsApiService

    @Test
    fun `send test announcement provides the right response`() {
        val announcementId = UUID.fromString("f1b3b3b3-1b3b-1b3b-1b3b-1b3b3b3b3b3b")
        val accountId = 69L;
        val externalAccountId = 12L
        every { accountsApiService.sendTestAnnouncement(accountId, 12) } answers {
            Mono.just(BasicAnnouncementWithIdDto(
                id = announcementId,
                type = AnnouncementType.TEST,
                author = accountId,
                title = "Test Announcement",
                content = "This is a test announcement.",
                link = "https://ryp.io",
            ))
        }

        val responseJson = loadJsonFromResource("sample-json/test-send-test-announcement-response.json")

        webClient.post()
            .uri("/accounts/$accountId/externalaccounts/$externalAccountId/test")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().valueEquals("Location", "null://null/announcements/$announcementId")
            .expectBody().json(responseJson)
    }

}
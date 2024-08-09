package io.vibrantnet.ryp.core.subscription.controller

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.subscription.service.ExternalAccountsApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono

@WebFluxTest(
    controllers = [EmailApiController::class, ApiExceptionHandler::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
class EmailApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun externalAccountsApiService() = mockk<ExternalAccountsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var externalAccountsApiService: ExternalAccountsApiService

    @Test
    fun `unsubscribing from email returns 204 and no content`() {
        every { externalAccountsApiService.unsubscribeFromEmail(any()) } answers { Mono.empty() }
        val requestJson = loadJsonFromResource("sample-json/test-unsubscribe-from-email-request.json")
        webClient.post()
            .uri("/email/unsubscribe")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty
    }
}
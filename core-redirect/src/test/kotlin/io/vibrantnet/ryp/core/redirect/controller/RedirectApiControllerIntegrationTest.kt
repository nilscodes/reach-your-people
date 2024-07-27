package io.vibrantnet.ryp.core.redirect.controller

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.redirect.service.RedirectApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(
    controllers = [RedirectApiController::class, ApiExceptionHandler::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
@ActiveProfiles("test")
class RedirectApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun redirectApiService() = mockk<RedirectApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var redirectApiService: RedirectApiService

    @Test
    fun `redirect location provided when shortcode has matching URL`() {
        every { redirectApiService.redirectToUrl("found") } answers {
            Mono.just("https://example.com")
        }

        webClient.get()
            .uri("/redirect/found")
            .exchange()
            .expectStatus().isPermanentRedirect
            .expectHeader().valueEquals("Location", "https://example.com")
            .expectHeader().valueEquals("Cache-Control", "max-age=86400")
    }

    @Test
    fun `not found error returned when shortcode is not found`() {
        every { redirectApiService.redirectToUrl("not-found") } answers {
            Mono.error(NoSuchElementException("No URL found for shortcode not-found"))
        }

        val responseJson = loadJsonFromResource("sample-json/test-redirect-not-found-response.json")

        webClient.get()
            .uri("/redirect/not-found")
            .exchange()
            .expectStatus().isNotFound
            .expectBody().json(responseJson)
    }

}
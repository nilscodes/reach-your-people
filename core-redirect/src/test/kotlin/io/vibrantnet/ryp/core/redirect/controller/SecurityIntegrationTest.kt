package io.vibrantnet.ryp.core.redirect.controller

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.redirect.service.UrlsApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("secured", "autoindexoff")
class SecurityIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun urlsApiService() = mockk<UrlsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var urlsApiService: UrlsApiService

    @Test
    fun `getting a specific projects urls is secured properly and returns the right object`() {
        val projectId = 69L
        every { urlsApiService.getUrlsForProject(projectId) } answers {
            Flux.fromIterable(listOf())
        }

        webClient.get()
            .uri("/urls/projects/$projectId")
            .headers { it.set(HttpHeaders.AUTHORIZATION, "deadend") }
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `not providing an API key results in a 401`() {
        webClient.get()
            .uri("/urls/projects/69")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `health endpoint is not secured`() {
        webClient.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk
    }

}
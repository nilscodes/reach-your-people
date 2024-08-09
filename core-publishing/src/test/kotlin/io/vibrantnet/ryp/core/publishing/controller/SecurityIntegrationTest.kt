package io.vibrantnet.ryp.core.publishing.controller

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.publishing.service.AnnouncementsApiService
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
        fun announcementsApiService() = mockk<AnnouncementsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var announcementsApiService: AnnouncementsApiService

    @Test
    fun `getting a specific projects announcements is secured properly and returns the right object`() {
        val projectId = 69L
        every { announcementsApiService.listAnnouncementsForProject(projectId) } answers {
            Flux.fromIterable(listOf())
        }

        webClient.get()
            .uri("/projects/$projectId/announcements")
            .headers { it.set(HttpHeaders.AUTHORIZATION, "deadend") }
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `not providing an API key results in a 401`() {
        webClient.get()
            .uri("/projects/69/announcements")
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
package io.vibrantnet.ryp.core.subscription.controller

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.subscription.service.ProjectsApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("secured", "test")
class SecurityIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun projectsApiService() = mockk<ProjectsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var projectsApiService: ProjectsApiService

    @Test
    fun `getting a specific project is secured properly and returns the right object`() {
        val projectId = 69L
        every { projectsApiService.getProject(projectId) } answers {
            Mono.just(makeProjectDto(projectId).copy(
                registrationTime = OffsetDateTime.parse("2021-09-01T00:00:00Z")
            ))
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-project-response.json")

        webClient.get()
            .uri("/projects/$projectId")
            .headers { it.set(HttpHeaders.AUTHORIZATION, "deadend") }
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `not providing an API key results in a 401`() {
        webClient.get()
            .uri("/projects")
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
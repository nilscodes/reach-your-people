package io.vibrantnet.ryp.core.subscription.controller

import io.mockk.every
import io.mockk.mockk
import io.ryp.shared.model.PolicyDto
import io.ryp.shared.model.ProjectCategory
import io.ryp.shared.model.ProjectDto
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.subscription.service.ProjectsApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@WebFluxTest(controllers = [ProjectsApiController::class, ApiExceptionHandler::class])
internal class ProjectsApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun projectsApiService() = mockk<ProjectsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var projectsApiService: ProjectsApiService

    @Test
    fun `create project works with correct payload`() {
        every { projectsApiService.addNewProject(any(), any()) } answers {
            Mono.just(makeProjectDto(69).copy(
                registrationTime = OffsetDateTime.parse("2021-09-01T00:00:00Z")
            ))
        }

        val requestJson = loadJsonFromResource("sample-json/test-create-project-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-create-project-response.json")

        webClient.post()
            .uri("/projects?projectOwner=12")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().valueEquals("Location", "/projects/69")
            .expectBody().json(responseJson)
    }

    @Test
    fun `create project fails if no project owner is provided`() {
        every { projectsApiService.addNewProject(any(), any()) } answers {
            Mono.just(makeProjectDto(69).copy(
                registrationTime = OffsetDateTime.parse("2021-09-01T00:00:00Z")
            ))
        }

        val requestJson = loadJsonFromResource("sample-json/test-create-project-request.json")

        webClient.post()
            .uri("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `listing projects works`() {
        every { projectsApiService.listProjects() } answers {
            Flux.fromIterable(listOf(
                makeProjectDto(69).copy(
                    registrationTime = OffsetDateTime.parse("2021-09-01T00:00:00Z")
                ),
                makeProjectDto(70).copy(
                    registrationTime = OffsetDateTime.parse("2021-09-02T00:00:00Z")
                )
            ))
        }

        val responseJson = loadJsonFromResource("sample-json/test-list-projects-response.json")

        webClient.get()
            .uri("/projects")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting a specific project works`() {
        val projectId = 69L
        every { projectsApiService.getProject(projectId) } answers {
            Mono.just(makeProjectDto(projectId).copy(
                registrationTime = OffsetDateTime.parse("2021-09-01T00:00:00Z")
            ))
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-project-response.json")

        webClient.get()
            .uri("/projects/$projectId")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `updating a project works with correct payload`() {
        val projectId = 69L
        every { projectsApiService.updateProject(projectId, any()) } answers {
            Mono.just(makeProjectDto(projectId).copy(
                registrationTime = OffsetDateTime.parse("2021-09-01T00:00:00Z"),
                tags = setOf("test", "nft"),
                manuallyVerified = OffsetDateTime.parse("2021-10-01T00:00:00Z")
            ))
        }

        val requestJson = loadJsonFromResource("sample-json/test-update-project-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-update-project-response.json")

        webClient.patch()
            .uri("/projects/$projectId")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }
}

fun makeProjectDto(projectId: Long?) = ProjectDto(
    id = projectId,
    name = "Test Project",
    description = "This is a test project",
    logo = "",
    url = "https://ryp.io/projects/$projectId",
    category = ProjectCategory.nFT,
    policies = setOf(
        PolicyDto("Test Policy", "df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058", null),
        PolicyDto("Test Policy 2", "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f", null),
    )
)
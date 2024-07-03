package io.vibrantnet.ryp.core.publishing.controller

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.publishing.model.*
import io.vibrantnet.ryp.core.publishing.service.AnnouncementsApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.util.*

@WebFluxTest(controllers = [ProjectsApiController::class, ApiExceptionHandler::class])
@ActiveProfiles("test")
class ProjectsApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun announcementsApiService() = mockk<AnnouncementsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var announcementsApiService: AnnouncementsApiService

    @Test
    fun `publish announcement shows right response if publish succeeds`() {
        val announcementId = UUID.fromString("f1b3b3b3-1b3b-1b3b-1b3b-1b3b3b3b3b3b")
        every { announcementsApiService.publishAnnouncementForProject(eq(15), any()) } answers {
            Mono.just(
                createSampleAnnouncement(announcementId)
            )
        }

        val requestJson = loadJsonFromResource("sample-json/test-publish-announcement-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-publish-announcement-response.json")

        webClient.post()
            .uri("/projects/15/announcements")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().valueEquals("Location", "null://null/announcements/$announcementId")
            .expectBody().json(responseJson, true)
    }

    @Test
    fun `publishing shows correct error if user is unauthorized`() {
        every { announcementsApiService.publishAnnouncementForProject(eq(15), any()) } answers {
            Mono.error(UserNotAuthorizedToPublishException("User with account ID 1 is not authorized to publish announcements for project 15"))
        }

        val requestJson = loadJsonFromResource("sample-json/test-publish-announcement-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-publish-announcement-unauthorized-response.json")

        webClient.post()
            .uri("/projects/15/announcements")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isForbidden
            .expectBody().json(responseJson)
    }

    private fun createSampleAnnouncement(announcementId: UUID) = AnnouncementDto(
        id = announcementId,
        projectId = 15,
        announcement = ActivityStream(
            id = "https://ryp.io/announcements/$announcementId",
            actor = Person(
                name = "Jeff Josh",
                id = "https://ryp.io/users/25"
            ),
            `object` = Note(
                content = "# Heading\n**Content**",
                summary = "Title",
                url = "https://somewhere.else"
            ),
            attributedTo = Organization(
                name = "RYP Project 15",
                id = "https://ryp.io/projects/15"
            ),
            published = ZonedDateTime.parse("2021-08-03T00:00:00Z")
        ),
        status = AnnouncementStatus.PENDING,
        shortLink = "https://go.ryp.io/abcdef",
        audience = Audience(listOf("abc")),
        statistics = Statistics(),
        createdDate = OffsetDateTime.parse("2021-08-01T00:00:00Z"),
        modifiedDate = OffsetDateTime.parse("2021-08-01T00:00:00Z")
    )

    @Test
    fun `listing announcements for a project works`() {
        every { announcementsApiService.listAnnouncementsForProject(15) } answers {
            Flux.fromIterable(
                listOf(
                    createSampleAnnouncement(UUID.fromString("f1b3b3b3-1b3b-1b3b-1b3b-1b3b3b3b3b3b")),
                    createSampleAnnouncement(UUID.fromString("f1b3b3b3-1b3b-1b3b-1b3b-1b4b3b3b3b3c"))
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-list-announcements-response.json")

        webClient.get()
            .uri("/projects/15/announcements")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting publishing permissions for a specific account within a project`() {
        every { announcementsApiService.getPublishingPermissionsForAccount(15, 3) } answers {
            Mono.just(
                PublishingPermissionsDto(
                    policies = listOf(
                        PolicyPublishingPermissionDto(
                            policyId = "1234567890123456789012345678901234567890123456789012345678901234",
                            permission = PublishingPermissionStatus.PUBLISHING_MANUAL
                        ),
                        PolicyPublishingPermissionDto(
                            policyId = "abcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcd",
                            permission = PublishingPermissionStatus.PUBLISHING_NOT_GRANTED
                        )
                    ),
                    accountId = 3,
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-publishing-permissions-response.json")

        webClient.get()
            .uri("/projects/15/roles/3")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }
}
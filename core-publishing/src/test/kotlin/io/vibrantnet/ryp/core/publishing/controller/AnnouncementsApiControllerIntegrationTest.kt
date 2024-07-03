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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.util.*
import kotlin.NoSuchElementException

@WebFluxTest(controllers = [AnnouncementsApiController::class, ApiExceptionHandler::class])
@ActiveProfiles("test")
class AnnouncementsApiControllerIntegrationTest {
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
    fun `get announcement provides the right response if announcement is present`() {
        val announcementId = UUID.fromString("f1b3b3b3-1b3b-1b3b-1b3b-1b3b3b3b3b3b")
        every { announcementsApiService.getAnnouncementById(announcementId) } answers {
            Mono.just(AnnouncementDto(
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
                status = AnnouncementStatus.PUBLISHED,
                shortLink = "https://go.ryp.io/abcdef",
                audience = Audience(listOf("abc")),
                statistics = Statistics(
                    sent = mapOf("twilio" to 2),
                    uniqueAccounts = 7L,
                    explicitSubscribers = 3L,
                    delivered = mapOf("twilio" to 2),
                    failures = mapOf("twilio" to 0),
                    views = mapOf("twilio" to 1)
                ),
                createdDate = OffsetDateTime.parse("2021-08-01T00:00:00Z"),
                modifiedDate = OffsetDateTime.parse("2021-08-02T00:00:00Z")
            ))
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-announcement-response.json")

        webClient.get()
            .uri("/announcements/$announcementId")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson, true)
    }

    @Test
    fun `getting announcement shows correct exception body when not found`() {
        val announcementId = UUID.fromString("f1b3b3b3-1b3b-1b3b-1b3b-1b3b3b3b3b3b")
        every { announcementsApiService.getAnnouncementById(announcementId) } answers {
            Mono.error(NoSuchElementException("Announcement with ID $announcementId not found."))
        }

        webClient.get()
            .uri("/announcements/$announcementId")
            .exchange()
            .expectStatus().isNotFound
            .expectBody().json(loadJsonFromResource("sample-json/test-get-announcement-notfound-response.json"))
    }
}
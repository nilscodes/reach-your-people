package io.vibrantnet.ryp.core.redirect.controller

import io.mockk.every
import io.mockk.mockk
import io.ryp.shared.model.ShortenedUrlDto
import io.ryp.shared.model.ShortenedUrlPartialDto
import io.ryp.shared.model.Status
import io.ryp.shared.model.Type
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.redirect.service.UrlsApiService
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
import java.util.*

@WebFluxTest(controllers = [UrlsApiController::class, ApiExceptionHandler::class])
@ActiveProfiles("test")
class UrlsApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun urlsApiService() = mockk<UrlsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var urlsApiService: UrlsApiService

    @Test
    fun `creating short URL works`() {
        val uuid = UUID.fromString("20000000-0000-0000-0000-000000000000")
        val shortcode = "new"
        every { urlsApiService.createShortUrl(any()) } answers {
            Mono.just(makeShortenedUrlDto(uuid, shortcode))
        }

        val requestJson = loadJsonFromResource("sample-json/test-create-short-url-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-create-short-url-response.json")

        webClient.post()
            .uri("/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().valueEquals("Location", "null://null/urls/$uuid")
            .expectHeader().valueEquals("X-Redirect-Location", "https://go.ryp.io/$shortcode")
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting URL by ID works`() {
        val uuid = UUID.fromString("00000000-0000-0000-0000-000000000000")
        every { urlsApiService.getUrlById(uuid.toString()) } answers {
            Mono.just(
                makeShortenedUrlDto(uuid, "short")
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-url-by-id-response.json")

        webClient.get()
            .uri("/urls/$uuid")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting URL by shortcode works`() {
        val shortcode = "supershort"
        every { urlsApiService.getUrlByShortcode(shortcode) } answers {
            Mono.just(
                makeShortenedUrlDto(UUID.fromString("10000000-0000-0000-0000-000000000000"), shortcode)
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-url-by-shortcode-response.json")

        webClient.get()
            .uri("/urls/shortcode/$shortcode")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting a list of shorturls for a project works`() {
        val projectId = 1L
        every { urlsApiService.getUrlsForProject(projectId) } answers {
            Flux.fromIterable(
                listOf(
                    makeShortenedUrlDto(UUID.fromString("40000000-0000-0000-0000-000000000000"), "one"),
                    makeShortenedUrlDto(UUID.fromString("50000000-0000-0000-0000-000000000000"), "two"),
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-urls-for-project-response.json")

        webClient.get()
            .uri("/urls/projects/$projectId")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `updating short URLs works`() {
        val uuid = UUID.fromString("30000000-0000-0000-0000-000000000000")
        val partial = ShortenedUrlPartialDto(
            status = Status.INACTIVE,
        )
        every { urlsApiService.updateUrlById(uuid.toString(), partial) } answers {
            Mono.just(makeShortenedUrlDto(uuid, "patch"))
        }

        val requestJson = loadJsonFromResource("sample-json/test-update-short-url-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-update-short-url-response.json")

        webClient.patch()
            .uri("/urls/$uuid")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    private fun makeShortenedUrlDto(uuid: UUID, shortcode: String) = ShortenedUrlDto(
        id = uuid.toString(),
        shortcode = shortcode,
        createTime = OffsetDateTime.parse("2021-08-01T00:00:00Z"),
        type = Type.EXTERNAL,
        status = Status.ACTIVE,
        url = "https://example.com",
        projectId = 1,
        views = 6,
    )

}
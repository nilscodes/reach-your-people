package io.vibrantnet.ryp.core.publishing.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ryp.shared.model.ShortenedUrlDto
import io.ryp.shared.model.Status
import io.ryp.shared.model.Type
import io.vibrantnet.ryp.core.publishing.CorePublishingConfiguration
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class RedirectServiceVibrantTest {
    private lateinit var redirectService: RedirectService

    @BeforeEach
    fun setUp() {
        redirectService = RedirectServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build(),
            CorePublishingConfiguration("", "", "", "https://ryp.io")
        )
    }

    @Test
    fun `short URL generation success returns proper object`() {
        val responseShortUrl = makeFullShortUrl()
        val shortUrlPayload = configureObjectMapper().writeValueAsString(responseShortUrl)
        mockBackend.enqueue(
            MockResponse().setResponseCode(200).setBody(shortUrlPayload).addHeader("Content-Type", "application/json")
        )
        val result = redirectService.createShortUrl(makeNewShortUrl())
        StepVerifier.create(result)
            .expectNext(responseShortUrl)
            .verifyComplete()
    }

    @Test
    fun `creating a short URL with fallback works with the shortened URL if the service responds successfully`() {
        val responseShortUrl = makeFullShortUrl()
        val shortUrlPayload = configureObjectMapper().writeValueAsString(responseShortUrl)
        mockBackend.enqueue(
            MockResponse().setResponseCode(200).setBody(shortUrlPayload)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Redirect-Location", "https://go.ryp.io/${responseShortUrl.shortcode}")
        )
        val result = redirectService.createShortUrlWithFallback("test")
        StepVerifier.create(result)
            .expectNext("https://go.ryp.io/${responseShortUrl.shortcode}")
            .verifyComplete()
    }

    @Test
    fun `creating a short URL with fallback works if the service does not respond at all`() {
        val brokenRedirectService = RedirectServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://luc:%s", mockBackend.port)).build(),
            CorePublishingConfiguration("", "", "", "https://ryp.io")
        )
        val result = brokenRedirectService.createShortUrlWithFallback("test")
        StepVerifier.create(result)
            .expectNext("https://ryp.io/test")
            .verifyComplete()
    }

    @Test
    fun `creating a short URL with fallback works if the service responds but has no redirect location custom header`() {
        val responseShortUrl = makeFullShortUrl()
        val shortUrlPayload = configureObjectMapper().writeValueAsString(responseShortUrl)
        mockBackend.enqueue(
            MockResponse().setResponseCode(200).setBody(shortUrlPayload)
                .addHeader("Content-Type", "application/json")
        )
        val result = redirectService.createShortUrlWithFallback("test2")
        StepVerifier.create(result)
            .expectNext("https://ryp.io/test2")
            .verifyComplete()
    }

    @Test
    fun `creating a short URL with fallback works if the service responds with a non-successful response`() {
        mockBackend.enqueue(
            MockResponse().setResponseCode(500)
        )
        val result = redirectService.createShortUrlWithFallback("worksregardless")
        StepVerifier.create(result)
            .expectNext("https://ryp.io/worksregardless")
            .verifyComplete()
    }

    private fun makeNewShortUrl() = ShortenedUrlDto(
        url = "test",
        projectId = 1,
        type = Type.RYP,
        status = Status.ACTIVE
    )

    private fun makeFullShortUrl() = ShortenedUrlDto(
        id = "1",
        shortcode = "test",
        createTime = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC),
        url = "test",
        projectId = 1,
        type = Type.RYP,
        status = Status.ACTIVE,
        views = 0
    )

    private fun configureObjectMapper(): ObjectMapper {
        return jacksonObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
    }

    companion object {
        lateinit var mockBackend: MockWebServer

        @JvmStatic
        @BeforeAll
        fun setUpClass() {
            mockBackend = MockWebServer()
            mockBackend.start()
        }

        @JvmStatic
        @AfterAll
        fun tearDownClass() {
            mockBackend.shutdown()
        }
    }
}

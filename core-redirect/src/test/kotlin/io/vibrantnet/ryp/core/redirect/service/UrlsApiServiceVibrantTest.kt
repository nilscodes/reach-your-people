package io.vibrantnet.ryp.core.redirect.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.ryp.shared.model.ShortenedUrlDto
import io.ryp.shared.model.ShortenedUrlPartialDto
import io.ryp.shared.model.Status
import io.ryp.shared.model.Type
import io.vibrantnet.ryp.core.redirect.persistence.ShortenedUrl
import io.vibrantnet.ryp.core.redirect.persistence.ShortenedUrlRepository
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.util.*

class UrlsApiServiceVibrantTest {
    @Test
    fun `creating a shortened URL works`() {
        val shortenedUrlRepository = mockk<ShortenedUrlRepository>()
        val urlsApiServiceVibrant = UrlsApiServiceVibrant(shortenedUrlRepository)
        val uuid = UUID.randomUUID()
        val shortcode = "brandnew"
        every { shortenedUrlRepository.save(any()) } answers {
            Mono.just(makeShortenedUrlDocument(uuid, shortcode))
        }
        val url = urlsApiServiceVibrant.createShortUrl(makeShortenedUrlDto(uuid, shortcode))
        StepVerifier.create(url)
            .expectNext(makeShortenedUrlDto(uuid, shortcode))
            .verifyComplete()
    }

    @Test
    fun `getting URL by ID works`() {
        val shortenedUrlRepository = mockk<ShortenedUrlRepository>()
        val urlsApiServiceVibrant = UrlsApiServiceVibrant(shortenedUrlRepository)
        val uuid = UUID.randomUUID()
        every { shortenedUrlRepository.findById(uuid.toString()) } answers {
            Mono.just(makeShortenedUrlDocument(uuid, "cool"))
        }
        val url = urlsApiServiceVibrant.getUrlById(uuid.toString())
        StepVerifier.create(url)
            .expectNext(makeShortenedUrlDto(uuid, "cool"))
            .verifyComplete()
    }

    @Test
    fun `getting URL by shortcode works`() {
        val shortenedUrlRepository = mockk<ShortenedUrlRepository>()
        val urlsApiServiceVibrant = UrlsApiServiceVibrant(shortenedUrlRepository)
        val uuid = UUID.randomUUID()
        val shortcode = "short"
        every { shortenedUrlRepository.findByShortcode(shortcode) } answers {
            Mono.just(makeShortenedUrlDocument(uuid, shortcode))
        }
        val url = urlsApiServiceVibrant.getUrlByShortcode(shortcode)
        StepVerifier.create(url)
            .expectNext(makeShortenedUrlDto(uuid, shortcode))
            .verifyComplete()
    }

    @Test
    fun `getting URLs by project ID works`() {
        val shortenedUrlRepository = mockk<ShortenedUrlRepository>()
        val urlsApiServiceVibrant = UrlsApiServiceVibrant(shortenedUrlRepository)
        val uuid1 = UUID.randomUUID()
        val shortcode1 = "short1"
        val uuid2 = UUID.randomUUID()
        val shortcode2 = "short2"
        val projectId = 15L
        every { shortenedUrlRepository.findByProjectId(projectId) } answers {
            Flux.fromIterable(
                listOf(
                    makeShortenedUrlDocument(uuid1, shortcode1),
                    makeShortenedUrlDocument(uuid2, shortcode2),
                )
            )
        }
        val url = urlsApiServiceVibrant.getUrlsForProject(projectId)
        StepVerifier.create(url)
            .expectNext(makeShortenedUrlDto(uuid1, shortcode1))
            .expectNext(makeShortenedUrlDto(uuid2, shortcode2))
            .verifyComplete()
    }

    @Test
    fun `updating a URL by ID works`() {
        val shortenedUrlRepository = mockk<ShortenedUrlRepository>()
        val urlsApiServiceVibrant = UrlsApiServiceVibrant(shortenedUrlRepository)
        val uuid = UUID.randomUUID()
        val shortcode = "short"
        val shortenedUrlPartialDto = ShortenedUrlPartialDto(
            type = Type.RYP,
            status = Status.INACTIVE,
            url = "derp",
        )
        every { shortenedUrlRepository.findById(uuid.toString()) } answers {
            Mono.just(makeShortenedUrlDocument(uuid, shortcode))
        }
        val slot = slot<ShortenedUrl>()
        every { shortenedUrlRepository.save(capture(slot)) } answers {
            Mono.just(slot.captured)
        }
        val updatedUrl = urlsApiServiceVibrant.updateUrlById(uuid.toString(), shortenedUrlPartialDto)
        StepVerifier.create(updatedUrl)
            .expectNext(makeShortenedUrlDto(uuid, shortcode).copy(
                type = Type.RYP,
                status = Status.INACTIVE,
                url = "derp",
            ))
            .verifyComplete()

    }

    private fun makeShortenedUrlDocument(uuid: UUID, shortcode: String) = ShortenedUrl(
        id = uuid.toString(),
        shortcode = shortcode,
        createTime = OffsetDateTime.parse("2021-08-01T00:00:00Z"),
        type = Type.EXTERNAL,
        status = Status.ACTIVE,
        url = "https://example.com",
        projectId = 1,
        views = 6,
    )

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
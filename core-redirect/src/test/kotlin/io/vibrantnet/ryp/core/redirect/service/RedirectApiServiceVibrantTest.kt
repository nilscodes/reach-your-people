package io.vibrantnet.ryp.core.redirect.service

import io.mockk.*
import io.ryp.shared.model.Status
import io.ryp.shared.model.Type
import io.vibrantnet.ryp.core.redirect.CoreRedirectConfiguration
import io.vibrantnet.ryp.core.redirect.persistence.ShortenedUrl
import io.vibrantnet.ryp.core.redirect.persistence.ShortenedUrlRepository
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

private val DEFAULT_CONFIGURATION = CoreRedirectConfiguration(
    baseUrl = "https://go.ryp.io",
    shortUrl = "https://go.ryp.io",
)

class RedirectApiServiceVibrantTest {
    @Test
    fun `redirecting to a standard external URL works`() {
        val shortenedUrlRepository = mockk<ShortenedUrlRepository>()
        val redirectApiService = RedirectApiServiceVibrant(shortenedUrlRepository, DEFAULT_CONFIGURATION)
        val shortcode = "external"
        val targetUrl = "https://www.google.com"
        every { shortenedUrlRepository.findByShortcode(shortcode) } answers {
            Mono.just(ShortenedUrl(UUID.randomUUID().toString(), targetUrl, shortcode, Type.EXTERNAL, Status.ACTIVE, 1, 0))
        }
        every { shortenedUrlRepository.save(any()) } answers { Mono.empty() }
        val redirect = redirectApiService.redirectToUrl(shortcode)
        StepVerifier.create(redirect)
            .expectNext(targetUrl)
            .verifyComplete()
    }

    @Test
    fun `redirecting to an internal RYP URL works`() {
        val shortenedUrlRepository = mockk<ShortenedUrlRepository>()
        val redirectApiService = RedirectApiServiceVibrant(shortenedUrlRepository, DEFAULT_CONFIGURATION)
        val shortcode = "internal"
        val targetUrl = "bootcamp/ryp"
        every { shortenedUrlRepository.findByShortcode(shortcode) } answers {
            Mono.just(ShortenedUrl(UUID.randomUUID().toString(), targetUrl, shortcode, Type.RYP, Status.ACTIVE, 1, 0))
        }
        every { shortenedUrlRepository.save(any()) } answers { Mono.empty() }
        val redirect = redirectApiService.redirectToUrl(shortcode)
        StepVerifier.create(redirect)
            .expectNext("${DEFAULT_CONFIGURATION.baseUrl}/$targetUrl")
            .verifyComplete()
    }

    @Test
    fun `views for a URL get increased when visited`() {
        val shortenedUrlRepository = mockk<ShortenedUrlRepository>()
        val redirectApiService = RedirectApiServiceVibrant(shortenedUrlRepository, DEFAULT_CONFIGURATION)
        val shortcode = "internal"
        val targetUrl = "bootcamp/ryp"
        every { shortenedUrlRepository.findByShortcode(shortcode) } answers {
            Mono.just(ShortenedUrl(UUID.randomUUID().toString(), targetUrl, shortcode, Type.RYP, Status.ACTIVE, 1, 15))
        }
        every {
            shortenedUrlRepository.save(match<ShortenedUrl> {
                it.views == 16L
            })
        } answers { Mono.empty() }
        val redirect = redirectApiService.redirectToUrl(shortcode)
        StepVerifier.create(redirect)
            .expectNext("${DEFAULT_CONFIGURATION.baseUrl}/$targetUrl")
            .verifyComplete()
    }
}
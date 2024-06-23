package io.vibrantnet.ryp.core.publishing.service

import io.ryp.shared.model.ShortenedUrlDto
import reactor.core.publisher.Mono

interface RedirectService {
    fun createShortUrl(shortenedUrl: ShortenedUrlDto): Mono<ShortenedUrlDto>
    fun createShortUrlWithFallback(relativeUrl: String): Mono<String>
}
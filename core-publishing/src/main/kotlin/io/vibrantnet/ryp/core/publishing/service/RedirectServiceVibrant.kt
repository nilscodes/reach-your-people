package io.vibrantnet.ryp.core.publishing.service

import io.ryp.shared.model.ShortenedUrlDto
import io.ryp.shared.model.Status
import io.ryp.shared.model.Type
import io.vibrantnet.ryp.core.publishing.CorePublishingConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class RedirectServiceVibrant(
    @Qualifier("coreRedirectClient")
    private val coreRedirectClient: WebClient,
    private val config: CorePublishingConfiguration,
) : RedirectService {
    override fun createShortUrl(shortenedUrl: ShortenedUrlDto): Mono<ShortenedUrlDto> {
        return coreRedirectClient.post()
            .uri("/urls")
            .bodyValue(shortenedUrl)
            .retrieve()
            .bodyToMono(ShortenedUrlDto::class.java)
    }

    /**
     * Create a short URL for the given relative URL. If the creation fails, return a fallback URL based on the RYP base URL.
     */
    override fun createShortUrlWithFallback(relativeUrl: String): Mono<String> {
        return coreRedirectClient.post()
            .uri("/urls")
            .bodyValue(
                ShortenedUrlDto(
                    url = relativeUrl,
                    projectId = 0,
                    type = Type.RYP,
                    status = Status.ACTIVE
                )
            )
            .exchangeToMono { response ->
                if (response.statusCode().is2xxSuccessful) {
                    val redirectLocation = response.headers().asHttpHeaders().getFirst("X-Redirect-Location")
                    if (redirectLocation != null) {
                        Mono.just(redirectLocation)
                    } else {
                        Mono.error(IllegalStateException("Missing X-Redirect-Location header"))
                    }
                } else {
                    logger.error { "Failed to create short URL for $relativeUrl: HTTP error code ${response.statusCode()}" }
                    response.createException().flatMap { Mono.error(it) }
                }
            }
            .onErrorResume { error ->
                logger.error(error) { "Failed to create short URL for $relativeUrl" }
                Mono.just("${config.baseUrl}/$relativeUrl")
            }
    }
}
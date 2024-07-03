package io.vibrantnet.ryp.core.redirect.service

import io.ryp.shared.model.Type
import io.vibrantnet.ryp.core.redirect.CoreRedirectConfiguration
import io.vibrantnet.ryp.core.redirect.persistence.ShortenedUrl
import io.vibrantnet.ryp.core.redirect.persistence.ShortenedUrlRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RedirectApiServiceVibrant(
    val shortenedUrlRepository: ShortenedUrlRepository,
    val configuration: CoreRedirectConfiguration
) : RedirectApiService {
    override fun redirectToUrl(shortcode: String): Mono<String> {
        return shortenedUrlRepository.findByShortcode(shortcode)
            .flatMap {
                shortenedUrlRepository.save(it.copy(views = it.views + 1))
                    .thenReturn(buildUrl(it))
            }
            .switchIfEmpty(Mono.error(NoSuchElementException("No URL found for shortcode $shortcode")))
    }

    fun buildUrl(shortenedUrl: ShortenedUrl) =
        if (shortenedUrl.type == Type.RYP) {
            "${configuration.baseUrl}/${shortenedUrl.url}"
        } else {
            shortenedUrl.url
        }
}
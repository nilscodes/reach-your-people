package io.vibrantnet.ryp.core.redirect.service

import io.ryp.shared.model.ShortenedUrlDto
import io.ryp.shared.model.ShortenedUrlPartialDto
import io.vibrantnet.ryp.core.redirect.model.DuplicateShortcodeException
import io.vibrantnet.ryp.core.redirect.persistence.ShortenedUrl
import io.vibrantnet.ryp.core.redirect.persistence.ShortenedUrlRepository
import io.vibrantnet.ryp.core.redirect.persistence.newEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

const val DEFAULT_SHORTCODE_LENGTH = 7;
const val SHORTCODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

@Service
class UrlsApiServiceVibrant(
    val shortenedUrlRepository: ShortenedUrlRepository,
): UrlsApiService {
    override fun createShortUrl(shortenedUrlDto: ShortenedUrlDto): Mono<ShortenedUrlDto> {
        val shortcode = shortenedUrlDto.shortcode ?: generateDefaultShortcode()
        return shortenedUrlRepository.findByShortcode(shortcode).flatMap {
            Mono.error<ShortenedUrl>(DuplicateShortcodeException("Shortcode $shortcode already exists"))
        }.switchIfEmpty(Mono.defer {
            val entity = newEntity(UUID.randomUUID(), shortcode, shortenedUrlDto)
            shortenedUrlRepository.save(entity)
        })
            .map { it.toDto() }
    }

    override fun getUrlById(urlId: String): Mono<ShortenedUrlDto> {
        return shortenedUrlRepository.findById(urlId)
            .map { it.toDto() }
    }

    override fun getUrlByShortcode(shortcode: String): Mono<ShortenedUrlDto> {
        return shortenedUrlRepository.findByShortcode(shortcode)
            .map { it.toDto() }
    }

    override fun getUrlsForProject(projectId: Long): Flux<ShortenedUrlDto> {
        return shortenedUrlRepository.findByProjectId(projectId)
            .map { it.toDto() }
    }

    override fun updateUrlById(urlId: String, shortenedUrlPartial: ShortenedUrlPartialDto): Mono<ShortenedUrlDto> {
        return shortenedUrlRepository.findById(urlId)
            .flatMap { existingUrl ->
                val newShortcode = shortenedUrlPartial.shortcode ?: existingUrl.shortcode
                val updatedUrl = existingUrl.copy(
                    shortcode = newShortcode,
                    url = shortenedUrlPartial.url ?: existingUrl.url,
                    type = shortenedUrlPartial.type ?: existingUrl.type,
                    status = shortenedUrlPartial.status ?: existingUrl.status
                )

                // Check if the shortcode has changed and if the new shortcode already exists
                if (newShortcode != existingUrl.shortcode) {
                    shortenedUrlRepository.findByShortcode(newShortcode).flatMap {
                        Mono.error<ShortenedUrl>(DuplicateShortcodeException("Shortcode $newShortcode already exists"))
                    }.switchIfEmpty(Mono.defer {
                        shortenedUrlRepository.save(updatedUrl)
                    })
                } else {
                    shortenedUrlRepository.save(updatedUrl)
                }
            }
            .map { it.toDto() }
    }

    private fun generateDefaultShortcode() = (1..DEFAULT_SHORTCODE_LENGTH)
            .map { SHORTCODE_CHARS.random() }
            .joinToString("")

}
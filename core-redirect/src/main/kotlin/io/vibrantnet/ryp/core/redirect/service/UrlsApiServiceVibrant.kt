package io.vibrantnet.ryp.core.redirect.service

import io.vibrantnet.ryp.core.redirect.model.ShortenedUrlDto
import io.vibrantnet.ryp.core.redirect.model.ShortenedUrlPartialDto
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
        val entity = newEntity(UUID.randomUUID(), shortcode, shortenedUrlDto)
        return shortenedUrlRepository.save(entity)
            .map { it.toDto() }
    }

    override fun getUrlById(urlId: String): Mono<ShortenedUrlDto> {
        return shortenedUrlRepository.findById(urlId)
            .map { it.toDto() }
    }

    override fun getUrlsForProject(projectId: Long): Flux<ShortenedUrlDto> {
        return shortenedUrlRepository.findByProjectId(projectId)
            .map { it.toDto() }
    }

    override fun updateUrlById(urlId: String, shortenedUrlPartial: ShortenedUrlPartialDto): Mono<ShortenedUrlDto> {
        return shortenedUrlRepository.findById(urlId)
            .map { it.copy(
                url = shortenedUrlPartial.url ?: it.url,
                type = shortenedUrlPartial.type ?: it.type,
                status = shortenedUrlPartial.status ?: it.status,
            ) }
            .flatMap { shortenedUrlRepository.save(it) }
            .map { it.toDto() }
    }

    private fun generateDefaultShortcode() = (1..DEFAULT_SHORTCODE_LENGTH)
            .map { SHORTCODE_CHARS.random() }
            .joinToString("")

}
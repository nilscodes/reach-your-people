package io.vibrantnet.ryp.core.redirect.persistence

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ShortenedUrlRepository: ReactiveCrudRepository<ShortenedUrl, String> {
    fun findByShortcode(shortcode: String): Mono<ShortenedUrl>
    fun findByProjectId(projectId: Long): Flux<ShortenedUrl>
}
package io.vibrantnet.ryp.core.redirect.service

import io.vibrantnet.ryp.core.redirect.model.ShortenedUrlDto
import io.vibrantnet.ryp.core.redirect.model.ShortenedUrlPartialDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UrlsApiService {

    /**
     * POST /urls : Create new and shortened redirect URL
     * Create a new, shortened URL and expose a redirect endpoint for it.
     *
     * @return Created (status code 201)
     * @see UrlsApi#createShortUrl
     */
    fun createShortUrl(shortenedUrlDto: ShortenedUrlDto): Mono<ShortenedUrlDto>

    /**
     * GET /urls/{urlId} : Get URL by ID
     * Get a shortened URL and its details by ID
     *
     * @param urlId The URL UUID (not the shortcode identifier used for the shortened URL itself) (required)
     * @return OK (status code 200)
     * @see UrlsApi#getUrlById
     */
    fun getUrlById(urlId: String): Mono<ShortenedUrlDto>

    /**
     * GET /urls/projects/{projectId} : Get all URLs for a project
     * Retrieve all active and inactive URLs for a given project
     *
     * @param projectId The numeric ID of a Project (required)
     * @return OK (status code 200)
     * @see UrlsApi#getUrlsForProject
     */
    fun getUrlsForProject(projectId: Long): Flux<ShortenedUrlDto>

    /**
     * PATCH /urls/{urlId} : Update URL details by ID
     * Update the details of a redirect URL by providing the new details and existing URL id
     *
     * @param urlId The URL UUID (not the shortcode identifier used for the shortened URL itself) (required)
     * @param shortenedUrlPartial  (optional)
     * @return OK (status code 200)
     * @see UrlsApi#updateUrlById
     */
    fun updateUrlById(urlId: String, shortenedUrlPartial: ShortenedUrlPartialDto): Mono<ShortenedUrlDto>
}

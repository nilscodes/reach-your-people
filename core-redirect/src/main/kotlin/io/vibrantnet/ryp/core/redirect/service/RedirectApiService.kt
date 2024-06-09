package io.vibrantnet.ryp.core.redirect.service

import reactor.core.publisher.Mono


fun interface RedirectApiService {

    /**
     * GET /redirect/{shortcode} : Redirect to URL
     * Redirects to the URL with the given shortcode if it is active, and updates any associated statistics.
     *
     * @param shortcode The shortcode of the URL to look up (required)
     * @return Moved Permanently (status code 301)
     * @see RedirectApi#redirectToUrl
     */
    fun redirectToUrl(shortcode: String): Mono<String>
}

package io.vibrantnet.ryp.core.redirect.controller

import io.vibrantnet.ryp.core.redirect.CoreRedirectConfiguration
import io.vibrantnet.ryp.core.redirect.service.RedirectApiService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class RedirectApiController(
    val service: RedirectApiService,
    val config: CoreRedirectConfiguration,
) {

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/redirect/{shortcode}"]
    )
    fun redirectToUrl( @PathVariable("shortcode") shortcode: String): Mono<ResponseEntity<Unit>> {
        return service.redirectToUrl(shortcode)
            .map {
                val headers = HttpHeaders()
                headers["Location"] = it
                if (config.redirect.maxCacheAge > 0) {
                    headers["Cache-Control"] = "max-age=${config.redirect.maxCacheAge}"
                }
                ResponseEntity(headers, HttpStatus.PERMANENT_REDIRECT)
            }
    }
}

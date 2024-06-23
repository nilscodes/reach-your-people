package io.vibrantnet.ryp.core.redirect.controller

import io.ryp.shared.model.ShortenedUrlDto
import io.ryp.shared.model.ShortenedUrlPartialDto
import io.vibrantnet.ryp.core.redirect.CoreRedirectConfiguration
import io.vibrantnet.ryp.core.redirect.service.UrlsApiService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class UrlsApiController(
    val service: UrlsApiService,
    val config: CoreRedirectConfiguration,
) {

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/urls"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun createShortUrl(
        @Validated @RequestBody shortenedUrl: ShortenedUrlDto,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<ShortenedUrlDto>> {
        return service.createShortUrl(shortenedUrl)
            .map { savedEntity ->
                ResponseEntity.created(exchange.request.uri.let {
                    UriComponentsBuilder.fromUriString(exchange.request.uri.scheme + "://" + exchange.request.uri.authority)
                        .path("/urls/{id}")
                        .buildAndExpand(savedEntity.id)
                        .toUri()
                })
                    .header("X-Redirect-Location", "${config.shortUrl}/${savedEntity.shortcode}")
                    .body(savedEntity)
            }
    }

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/urls/{urlId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getUrlById(@PathVariable("urlId") urlId: String) = service.getUrlById(urlId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/urls/projects/{projectId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getUrlsForProject(@PathVariable("projectId") projectId: Long) = service.getUrlsForProject(projectId)

    @RequestMapping(
        method = [RequestMethod.PATCH],
        value = ["/urls/{urlId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun updateUrlById(
        @PathVariable("urlId") urlId: String,
        @Validated @RequestBody shortenedUrlPartial: ShortenedUrlPartialDto
    ) = service.updateUrlById(urlId, shortenedUrlPartial)
}

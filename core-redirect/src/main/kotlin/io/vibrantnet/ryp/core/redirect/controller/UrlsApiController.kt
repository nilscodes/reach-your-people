package io.vibrantnet.ryp.core.redirect.controller

import io.vibrantnet.ryp.core.redirect.model.ShortenedUrlDto
import io.vibrantnet.ryp.core.redirect.model.ShortenedUrlPartialDto
import io.vibrantnet.ryp.core.redirect.service.UrlsApiService
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class UrlsApiController(val service: UrlsApiService) {


    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/urls"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun createShortUrl(@Validated @RequestBody shortenedUrl: ShortenedUrlDto) = service.createShortUrl(shortenedUrl)


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

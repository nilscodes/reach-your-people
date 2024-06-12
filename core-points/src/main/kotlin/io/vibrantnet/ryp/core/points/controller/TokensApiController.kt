package io.vibrantnet.ryp.core.points.controller

import io.vibrantnet.ryp.core.points.model.PointsTokenDto
import io.vibrantnet.ryp.core.points.service.TokensApiService
import jakarta.validation.Valid
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
class TokensApiController(
    val service: TokensApiService
) {


    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/tokens"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun createPointsToken(
        @Valid @RequestBody(required = false) pointsTokenDto: PointsTokenDto,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<PointsTokenDto>> {
        return service.createPointsToken(pointsTokenDto)
            .map { savedEntity ->
                ResponseEntity.created(exchange.request.uri.let {
                    UriComponentsBuilder.fromUri(it).path("/{id}").buildAndExpand(savedEntity.id).toUri()
                })
                    .body(savedEntity)
            }
    }

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/tokens/{tokenId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getPointsToken(@PathVariable("tokenId") tokenId: Int) = service.getPointsToken(tokenId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/tokens"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun listPointsTokens() = service.listPointsTokens()
}

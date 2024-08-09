package io.vibrantnet.ryp.core.verification.controller

import io.ryp.cardano.model.StakepoolVerificationDto
import io.vibrantnet.ryp.core.verification.service.PoolsApiService
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
class PoolsApiController(val service: PoolsApiService) {

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/pools/{poolHash}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getStakepoolDetails(@PathVariable("poolHash") poolHash: String) = service.getStakepoolDetails(poolHash)

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/pools/{poolHash}/verifications"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun startStakepoolVerification(
        @PathVariable("poolHash") poolHash: String,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<StakepoolVerificationDto>> {
        return service.startStakepoolVerification(poolHash)
            .map { savedEntity ->
                ResponseEntity.created(exchange.request.uri.let {
                    UriComponentsBuilder.fromUri(it).path("/{verificationNonce}").buildAndExpand(savedEntity.nonce)
                        .toUri()
                })
                    .body(savedEntity)
            }
    }

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/pools/{poolHash}/verifications/{verificationNonce}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun testStakepoolVerification(
        @PathVariable("poolHash") poolHash: String,
        @PathVariable("verificationNonce") verificationNonce: String,
        @Valid @RequestBody stakepoolVerification: StakepoolVerificationDto,
    ) = service.testStakepoolVerification(poolHash, verificationNonce, stakepoolVerification)

    @RequestMapping(
        method = [RequestMethod.PUT],
        value = ["/pools/{poolHash}/verifications/{verificationNonce}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun completeStakepoolVerification(
        @PathVariable("poolHash") poolHash: String,
        @PathVariable("verificationNonce") verificationNonce: String,
        @Valid @RequestBody stakepoolVerification: StakepoolVerificationDto,
    ) = service.completeStakepoolVerification(poolHash, verificationNonce, stakepoolVerification)
}

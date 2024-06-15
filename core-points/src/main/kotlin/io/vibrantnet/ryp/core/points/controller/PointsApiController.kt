package io.vibrantnet.ryp.core.points.controller

import io.ryp.shared.model.points.PointsClaimDto
import io.ryp.shared.model.points.PointsClaimPartialDto
import io.vibrantnet.ryp.core.points.service.PointsApiService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class PointsApiController(
    val service: PointsApiService
) {


    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/points/accounts/{accountId}/claims/{tokenId}/{claimId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun createPointClaim(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("tokenId") tokenId: Int,
        @PathVariable("claimId") claimId: String,
        @Valid @RequestBody pointsClaimDto: PointsClaimDto,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<PointsClaimDto>> {
        return service.createPointClaim(accountId, tokenId, claimId, pointsClaimDto)
            .map { savedEntity ->
                ResponseEntity.created(exchange.request.uri)
                    .body(savedEntity)
            }
    }


    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/points/accounts/{accountId}/claims"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getPointClaimsForAccount(@PathVariable("accountId") accountId: Long) =
        service.getPointClaimsForAccount(accountId)


    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/points/accounts/{accountId}/claims/{tokenId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getPointClaimsForAccountAndToken(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("tokenId") tokenId: Int
    ) = service.getPointClaimsForAccountAndToken(accountId, tokenId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/points/accounts/{accountId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getPointsSummaryForAccount(@PathVariable("accountId") accountId: Long) =
        service.getPointsSummaryForAccount(accountId)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/points/accounts/{accountId}/claims/{tokenId}/{claimId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getSpecificPointClaimForAccountAndToken(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("tokenId") tokenId: Int,
        @PathVariable("claimId") claimId: String
    ) = service.getSpecificPointClaimForAccountAndToken(accountId, tokenId, claimId)

    @RequestMapping(
        method = [RequestMethod.PATCH],
        value = ["/points/accounts/{accountId}/claims/{tokenId}/{claimId}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun updatePointClaim(
        @PathVariable("accountId") accountId: Long,
        @PathVariable("tokenId") tokenId: Int,
        @PathVariable("claimId") claimId: String,
        @Valid @RequestBody pointsClaimPartialDto: PointsClaimPartialDto
    ) = service.updatePointClaim(accountId, tokenId, claimId, pointsClaimPartialDto)
}

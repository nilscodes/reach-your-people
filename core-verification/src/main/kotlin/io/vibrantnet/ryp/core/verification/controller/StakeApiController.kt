package io.vibrantnet.ryp.core.verification.controller

import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import io.vibrantnet.ryp.core.verification.service.StakeApiService
import jakarta.validation.constraints.Pattern
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@Validated
@RequestMapping("\${api.base-path:}")
@CrossOrigin
class StakeApiController(@Autowired val service: StakeApiService) {

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/stake/{stakeAddress}/assetcounts"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getMultiAssetCountForStakeAddress(@PathVariable("stakeAddress") stakeAddress: String): Flux<TokenOwnershipInfoWithAssetCount> =
        service.getMultiAssetCountForStakeAddress(stakeAddress)

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/stake/{stakeAddress}/pool"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getStakepoolDetailsForStakeAddress(@PathVariable("stakeAddress") stakeAddress: String) =
        service.getStakepoolDetailsForStakeAddress(stakeAddress)
}

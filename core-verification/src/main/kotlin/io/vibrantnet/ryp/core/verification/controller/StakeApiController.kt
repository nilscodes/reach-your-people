package io.vibrantnet.ryp.core.verification.controller

import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import io.vibrantnet.ryp.core.verification.service.StakeApiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
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

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/stake/{stakeAddress}/drep"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getDrepDetailsForStakeAddress(@PathVariable("stakeAddress") stakeAddress: String) =
        service.getDrepDetailsForStakeAddress(stakeAddress)
}

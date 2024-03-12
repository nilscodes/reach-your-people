package io.vibrantnet.ryp.core.verification.controller

import io.ryp.shared.model.TokenOwnershipInfoWithAssetCount
import io.vibrantnet.ryp.core.verification.service.StakeApiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
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
    fun getMultiAssetCountForStakeAddress(@PathVariable("stakeAddress") stakeAddress: String): Flux<TokenOwnershipInfoWithAssetCount> = service.getMultiAssetCountForStakeAddress(stakeAddress)
}

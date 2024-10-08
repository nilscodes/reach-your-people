package io.vibrantnet.ryp.core.subscription.service

import io.ryp.cardano.model.governance.DRepDetailsDto
import io.ryp.cardano.model.stakepools.StakepoolDetailsDto
import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface VerifyService {
    fun getPoliciesInWallet(
        stakeAddress: String,
    ): Flux<TokenOwnershipInfoWithAssetCount>

    fun getStakepoolDetailsForStakeAddress(
        stakeAddress: String,
    ): Mono<StakepoolDetailsDto>

    fun getDRepDetailsForStakeAddress(
        stakeAddress: String,
    ): Mono<DRepDetailsDto>
}
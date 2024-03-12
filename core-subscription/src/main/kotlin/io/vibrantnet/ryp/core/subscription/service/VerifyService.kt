package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.model.TokenOwnershipInfoWithAssetCount
import reactor.core.publisher.Flux

fun interface VerifyService {
    fun getPoliciesInWallet(
        stakeAddress: String,
    ): Flux<TokenOwnershipInfoWithAssetCount>
}
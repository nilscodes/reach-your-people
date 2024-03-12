package io.vibrantnet.ryp.core.verification.service

import io.ryp.shared.model.TokenOwnershipInfoWithAssetCount
import reactor.core.publisher.Flux

fun interface StakeApiService {

    /**
     * GET /stake/{stakeAddress}/assetcounts : Get policy IDs and asset counts for a stake address
     *
     * @param stakeAddress The staking address of a wallet in view format (required)
     * @return A list of token policies and associated asset counts currently owned by the stake address (status code 200)
     * @see StakeApi#getMultiAssetCountForStakeAddress
     */
    fun getMultiAssetCountForStakeAddress(stakeAddress: String): Flux<TokenOwnershipInfoWithAssetCount>
}
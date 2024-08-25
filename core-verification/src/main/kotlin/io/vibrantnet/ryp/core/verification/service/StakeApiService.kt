package io.vibrantnet.ryp.core.verification.service

import io.ryp.cardano.model.governance.DRepDetailsDto
import io.ryp.cardano.model.stakepools.StakepoolDetailsDto
import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface StakeApiService {

    /**
     * GET /stake/{stakeAddress}/assetcounts : Get policy IDs and asset counts for a stake address
     *
     * @param stakeAddress The staking address of a wallet in view format (required)
     * @return A list of token policies and associated asset counts currently owned by the stake address (status code 200)
     * @see StakeApi#getMultiAssetCountForStakeAddress
     */
    fun getMultiAssetCountForStakeAddress(stakeAddress: String): Flux<TokenOwnershipInfoWithAssetCount>

    /**
     * GET /stake/{stakeAddress}/pool : Get stake pool details for a given stake address, if currently staked
     *
     * @param stakeAddress The staking address of a wallet in view format (required)
     * @return Not found if the stake address is not found or currently not delegated (it would be nice to separate those two cases by an error, but it is not currently possible) (status code 404)
     *         or The stakepool details for this particular stake address (status code 200)
     * @see StakeApi#getStakepoolDetailsForStakeAddress
     */
    fun getStakepoolDetailsForStakeAddress(stakeAddress: String): Mono<StakepoolDetailsDto>

    /**
     * GET /stake/{stakeAddress}/drep : Get dRep details for a given stake address, if currently delegated
     *
     * @param stakeAddress The staking address of a wallet in view format (required)
     * @return The dRep details for this particular stake address (status code 200)
     *         or Not found if the stake address is not found or currently not delegated (it would be nice to separate those two cases by an error, but it is not currently possible) (status code 404)
     * @see StakeApi#getDrepDetailsForStakeAddress
     */
    fun getDrepDetailsForStakeAddress(stakeAddress: String): Mono<DRepDetailsDto>
}
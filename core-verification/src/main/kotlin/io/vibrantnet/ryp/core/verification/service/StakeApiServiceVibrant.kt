package io.vibrantnet.ryp.core.verification.service

import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import io.vibrantnet.ryp.core.verification.persistence.StakepoolDao
import io.vibrantnet.ryp.core.verification.persistence.TokenDao
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class StakeApiServiceVibrant(
    val tokenDao: TokenDao,
    val stakepoolDao: StakepoolDao,
) : StakeApiService {
    override fun getMultiAssetCountForStakeAddress(stakeAddress: String): Flux<TokenOwnershipInfoWithAssetCount> =
        Flux.fromIterable(tokenDao.getMultiAssetListForStakeAddress(stakeAddress))

    override fun getStakepoolDetailsForStakeAddress(stakeAddress: String) =
        stakepoolDao.getStakepoolDetailsForStakeAddress(stakeAddress)
}
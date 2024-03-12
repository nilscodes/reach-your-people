package io.vibrantnet.ryp.core.verification.service

import io.ryp.shared.model.TokenOwnershipInfoWithAssetCount
import io.vibrantnet.ryp.core.verification.persistence.TokenDao
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class StakeApiServiceVibrant(
    val tokenDao: TokenDao,
) : StakeApiService {
    override fun getMultiAssetCountForStakeAddress(stakeAddress: String): Flux<TokenOwnershipInfoWithAssetCount> =
        Flux.fromIterable(tokenDao.getMultiAssetListForStakeAddress(stakeAddress))
}
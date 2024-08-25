package io.vibrantnet.ryp.core.verification.persistence

import io.ryp.cardano.model.stakepools.StakepoolDelegationInfoDto
import io.ryp.cardano.model.stakepools.StakepoolDetailsDto
import io.vibrantnet.ryp.core.verification.model.PartialPoolMetadata
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "blockfrost")
class StakepoolDaoBlockfrost(
    @Qualifier("blockfrostClient") private val blockfrostClient: WebClient,
) : StakepoolDao {
    override fun getStakepoolDetails(poolHash: String): Mono<StakepoolDetailsDto> {
        return blockfrostClient.get()
            .uri("/pools/$poolHash/metadata")
            .retrieve()
            .onStatus({ status -> status == HttpStatus.NOT_FOUND }) { _ ->
                Mono.error(NoSuchElementException("Pool metadata not found in Blockfrost for pool hash $poolHash."))
            }
            .bodyToMono(PartialPoolMetadata::class.java)
            .map {
                StakepoolDetailsDto(
                    poolHash = it.poolHash,
                    ticker = it.ticker,
                    name = it.name,
                    description = it.description,
                    homepage = it.homepage,
                )
            }
    }

    override fun getStakepoolDetailsForStakeAddress(stakeAddress: String): Mono<StakepoolDetailsDto> {
        TODO("Not yet implemented")
    }

    override fun getActiveDelegation(poolHash: String): List<StakepoolDelegationInfoDto> {
        TODO("Not yet implemented")
    }

    override fun getActiveDelegationWithoutAmount(poolHash: String): List<StakepoolDelegationInfoDto> {
        TODO("Not yet implemented")
    }

    override fun getDelegationInEpoch(poolHash: String, epochNo: Int): List<StakepoolDelegationInfoDto> {
        TODO("Not yet implemented")
    }
}
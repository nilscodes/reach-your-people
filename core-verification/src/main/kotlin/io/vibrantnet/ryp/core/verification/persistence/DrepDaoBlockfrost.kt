package io.vibrantnet.ryp.core.verification.persistence

import io.ryp.cardano.model.DRepDelegationInfoDto
import io.ryp.cardano.model.DRepDetailsDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "blockfrost")
class DrepDaoBlockfrost(
    @Qualifier("blockfrostClient") private val blockfrostClient: WebClient,
) : DrepDao {
    override fun getDrepDetails(drepId: String): Mono<DRepDetailsDto> {
        TODO("Not yet implemented")
    }

    override fun getDrepDetailsForStakeAddress(stakeAddress: String): Mono<DRepDetailsDto> {
        TODO("Not yet implemented")
    }

    override fun getActiveDelegationWithoutAmount(drepId: String): List<DRepDelegationInfoDto> {
        TODO("Not yet implemented")
    }
}
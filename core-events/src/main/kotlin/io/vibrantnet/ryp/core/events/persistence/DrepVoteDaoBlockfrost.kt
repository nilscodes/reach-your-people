package io.vibrantnet.ryp.core.events.persistence

import io.ryp.cardano.model.DRepDelegationInfoDto
import io.ryp.cardano.model.DRepDetailsDto
import io.vibrantnet.ryp.core.events.model.DRepVoteDetailsDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "blockfrost")
class DrepVoteDaoBlockfrost(
    @Qualifier("blockfrostClient") private val blockfrostClient: WebClient,
) : DrepVoteDao {
    override fun getDrepVotesWithIdsHigherThan(voteId: Long): Flux<DRepVoteDetailsDto> {
        TODO("Not yet implemented")
    }
}
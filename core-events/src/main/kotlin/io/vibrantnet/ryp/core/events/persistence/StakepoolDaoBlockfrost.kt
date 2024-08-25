package io.vibrantnet.ryp.core.events.persistence

import io.vibrantnet.ryp.core.events.model.StakepoolRetirementDto
import io.vibrantnet.ryp.core.events.model.StakepoolVoteDetailsDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "blockfrost")
class StakepoolDaoBlockfrost(
    @Qualifier("blockfrostClient") private val blockfrostClient: WebClient,
) : StakepoolDao {
    override fun getStakepoolRetirementsWithIdsHigherThan(voteId: Long): Flux<StakepoolRetirementDto> {
        TODO("Not yet implemented")
    }

    override fun getStakepoolVotesWithIdsHigherThan(voteId: Long): Flux<StakepoolVoteDetailsDto> {
        TODO("Not yet implemented")
    }
}
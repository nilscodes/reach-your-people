package io.vibrantnet.ryp.core.events.persistence

import io.vibrantnet.ryp.core.events.model.GovernanceActionProposalDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "blockfrost")
class GovernanceActionDaoBlockfrost(
    @Qualifier("blockfrostClient") private val blockfrostClient: WebClient,
) : GovernanceActionDao {
    override fun getGovernanceActionsWithIdsHigherThan(voteId: Long): Flux<GovernanceActionProposalDto> {
        TODO("Not yet implemented")
    }
}
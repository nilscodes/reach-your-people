package io.vibrantnet.ryp.core.events.persistence

import io.vibrantnet.ryp.core.events.model.GovernanceActionProposalDto
import reactor.core.publisher.Flux

fun interface GovernanceActionDao {
    fun getGovernanceActionsWithIdsHigherThan(voteId: Long): Flux<GovernanceActionProposalDto>
}
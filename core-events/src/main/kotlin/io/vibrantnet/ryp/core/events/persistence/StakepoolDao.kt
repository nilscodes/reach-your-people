package io.vibrantnet.ryp.core.events.persistence

import io.vibrantnet.ryp.core.events.model.StakepoolRetirementDto
import io.vibrantnet.ryp.core.events.model.StakepoolVoteDetailsDto
import reactor.core.publisher.Flux

interface StakepoolDao {
    fun getStakepoolRetirementsWithIdsHigherThan(voteId: Long): Flux<StakepoolRetirementDto>
    fun getStakepoolVotesWithIdsHigherThan(voteId: Long): Flux<StakepoolVoteDetailsDto>
}
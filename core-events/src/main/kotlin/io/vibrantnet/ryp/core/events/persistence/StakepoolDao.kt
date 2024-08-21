package io.vibrantnet.ryp.core.events.persistence

import io.vibrantnet.ryp.core.events.model.StakepoolRetirementDto
import reactor.core.publisher.Flux

interface StakepoolDao {
    fun getStakepoolRetirementsWithIdsHigherThan(voteId: Long): Flux<StakepoolRetirementDto>
}
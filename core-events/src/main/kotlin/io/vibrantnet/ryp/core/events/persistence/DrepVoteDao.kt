package io.vibrantnet.ryp.core.events.persistence

import io.vibrantnet.ryp.core.events.model.DRepVoteDetailsDto
import reactor.core.publisher.Flux

fun interface DrepVoteDao {
    fun getDrepVotesWithIdsHigherThan(voteId: Long): Flux<DRepVoteDetailsDto>
}
package io.vibrantnet.ryp.core.events.model

import io.ryp.cardano.model.EventNotification
import io.ryp.cardano.model.EventNotificationType
import io.ryp.shared.model.Audience

data class StakepoolVoteDetailsDto(
    val id: Long,
    val transactionHash: String,
    val transactionIndex: Int,
    val proposalId: Long,
    val poolHash: String,
    val votingAnchorUrl: String?
) {
    fun toEventNotification(voteComment: String?): EventNotification {
        return EventNotification(
            type = EventNotificationType.GOVERNANCE_VOTE,
            transactionHash = transactionHash,
            transactionIndex = transactionIndex,
            audience = Audience(
                stakepools = listOf(poolHash),
            ),
            metadata = mapOf("comment" to (voteComment ?: ""))
        )

    }
}
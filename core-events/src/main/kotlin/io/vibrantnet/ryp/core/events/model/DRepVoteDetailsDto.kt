package io.vibrantnet.ryp.core.events.model

import io.ryp.cardano.model.EventNotification
import io.ryp.cardano.model.EventNotificationType
import io.ryp.shared.model.Audience

data class DRepVoteDetailsDto(
    val id: Long,
    val transactionHash: String,
    val proposalId: Long,
    val drepId: String,
    val votingAnchorUrl: String?
) {
    fun toEventNotification(voteComment: String?): EventNotification {
        return EventNotification(
            type = EventNotificationType.GOVERNANCE_VOTE,
            transactionHash = transactionHash,
            audience = Audience(
                dreps = listOf(drepId),
            ),
            comment = voteComment
        )

    }
}
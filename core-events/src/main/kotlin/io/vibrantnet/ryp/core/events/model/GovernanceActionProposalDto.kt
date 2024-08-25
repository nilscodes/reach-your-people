package io.vibrantnet.ryp.core.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import io.ryp.cardano.model.EventNotification
import io.ryp.cardano.model.EventNotificationType
import io.ryp.cardano.model.governance.GovernanceActionType
import io.ryp.shared.model.Audience
import io.ryp.shared.model.GlobalAnnouncementAudience

data class GovernanceActionProposalDto @JsonCreator constructor(
    val proposalId: Long,
    val transactionHash: String,
    val transactionIndex: Int,
    val type: GovernanceActionType,
    val votingAnchorUrl: String?
) {
    fun toEventNotification(proposalTitle: String?): EventNotification {
        return EventNotification(
            type = EventNotificationType.GOVERNANCE_ACTION_NEW_PROPOSAL,
            transactionHash = transactionHash,
            transactionIndex = transactionIndex,
            audience = Audience(
                global = listOf(GlobalAnnouncementAudience.GOVERNANCE_CARDANO)
            ),
            metadata = mapOf(
                "title" to (proposalTitle ?: ""),
                "type" to type.name,
            )
        )

    }
}

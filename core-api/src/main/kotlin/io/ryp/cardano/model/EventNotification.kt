package io.ryp.cardano.model

import com.fasterxml.jackson.annotation.JsonInclude
import io.ryp.shared.model.Audience

data class EventNotification(
    val type: EventNotificationType,
    val transactionHash: String,
    val transactionIndex: Int,
    val audience: Audience,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val metadata: Map<String, String> = emptyMap(),
)

enum class EventNotificationType {
    GOVERNANCE_VOTE,
    STAKEPOOL_RETIREMENT,
    GOVERNANCE_ACTION_NEW_PROPOSAL,
}
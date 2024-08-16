package io.ryp.cardano.model

import io.ryp.shared.model.Audience

data class EventNotification(
    val type: EventNotificationType,
    val transactionHash: String,
    val audience: Audience,
    val comment: String? = null,
)

enum class EventNotificationType {
    GOVERNANCE_VOTE,
}
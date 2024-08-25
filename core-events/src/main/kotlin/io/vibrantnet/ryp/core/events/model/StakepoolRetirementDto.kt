package io.vibrantnet.ryp.core.events.model

import io.ryp.cardano.model.EventNotification
import io.ryp.cardano.model.EventNotificationType
import io.ryp.shared.model.Audience

data class StakepoolRetirementDto(
    val id: Long,
    val transactionHash: String,
    val transactionIndex: Int,
    val poolHash: String,
) {
    fun toEventNotification(): EventNotification {
        return EventNotification(
            type = EventNotificationType.STAKEPOOL_RETIREMENT,
            transactionHash = transactionHash,
            transactionIndex = transactionIndex,
            audience = Audience(
                stakepools = listOf(poolHash),
            ),
        )

    }
}
package io.vibrantnet.ryp.core.events.model

import io.ryp.cardano.model.EventNotification
import io.ryp.cardano.model.EventNotificationType
import io.ryp.shared.model.Audience
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class StakepoolRetirementDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(StakepoolRetirementDto::class.java)
            .verify()
    }

    @Test
    fun `toEventNotification should return EventNotification with correct values`() {
        val actual = StakepoolRetirementDto(
            id = 1,
            transactionHash = "transactionHash",
            poolHash = "poolHash"
        ).toEventNotification()

        Assertions.assertEquals(EventNotification(
            type = EventNotificationType.STAKEPOOL_RETIREMENT,
            transactionHash = "transactionHash",
            audience = Audience(
                stakepools = listOf("poolHash"),
            )
        ), actual)
    }
}
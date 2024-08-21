package io.vibrantnet.ryp.core.events.model

import io.ryp.cardano.model.EventNotification
import io.ryp.cardano.model.EventNotificationType
import io.ryp.shared.model.Audience
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DRepVoteDetailsDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(DRepVoteDetailsDto::class.java)
            .verify()
    }

    @Test
    fun `toEventNotification should return EventNotification with correct values`() {
        val actual = DRepVoteDetailsDto(
            id = 1,
            transactionHash = "transactionHash",
            proposalId = 2,
            drepId = "drepId",
            votingAnchorUrl = "votingAnchorUrl"
        ).toEventNotification("voteComment")

        Assertions.assertEquals(EventNotification(
            type = EventNotificationType.GOVERNANCE_VOTE,
            transactionHash = "transactionHash",
            audience = Audience(
                dreps = listOf("drepId"),
            ),
            comment = "voteComment"
        ), actual)
    }
}
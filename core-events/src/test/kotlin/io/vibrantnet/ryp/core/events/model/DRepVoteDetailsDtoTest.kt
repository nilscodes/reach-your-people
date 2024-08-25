package io.vibrantnet.ryp.core.events.model

import io.ryp.cardano.model.EventNotification
import io.ryp.cardano.model.EventNotificationType
import io.ryp.shared.model.Audience
import io.vibrantnet.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class DRepVoteDetailsDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(DRepVoteDetailsDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = DRepVoteDetailsDto(
            id = 1,
            drepId = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
            transactionHash = "5e0e59a1e40fbdef987d0fdb47fd08a6f00ba6a1661b26ebae7bd827ec400e50",
            transactionIndex = 1,
            proposalId = 2,
            votingAnchorUrl = "https://voting-anchor-url.com",
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "id": 1,
                "drepId": "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
                "transactionHash": "5e0e59a1e40fbdef987d0fdb47fd08a6f00ba6a1661b26ebae7bd827ec400e50",
                "transactionIndex": 1,
                "proposalId": 2,
                "votingAnchorUrl": "https://voting-anchor-url.com"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )
    }

    @Test
    fun `toEventNotification should return EventNotification with correct values`() {
        val actual = DRepVoteDetailsDto(
            id = 1,
            transactionHash = "transactionHash",
            transactionIndex = 1,
            proposalId = 2,
            drepId = "drepId",
            votingAnchorUrl = "votingAnchorUrl"
        ).toEventNotification("voteComment")

        Assertions.assertEquals(EventNotification(
            type = EventNotificationType.GOVERNANCE_VOTE,
            transactionHash = "transactionHash",
            transactionIndex = 1,
            audience = Audience(
                dreps = listOf("drepId"),
            ),
            metadata = mapOf("comment" to "voteComment")
        ), actual)
    }
}
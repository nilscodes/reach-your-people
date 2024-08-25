package io.ryp.cardano.model

import io.ryp.core.createDefaultObjectMapper
import io.ryp.shared.model.Audience
import io.ryp.shared.model.GlobalAnnouncementAudience
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class EventNotificationTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(EventNotification::class.java)
            .verify()
    }

    @Test
    fun `serialization does not include metadata if not provided`() {
        val dto = EventNotification(
            type = EventNotificationType.GOVERNANCE_VOTE,
            transactionHash = "transactionHash",
            transactionIndex = 1,
            audience = Audience(
                policies = listOf("policyId"),
                stakepools = listOf("stakepool"),
                dreps = listOf("drep"),
                global = listOf(GlobalAnnouncementAudience.GOVERNANCE_CARDANO),
            )
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "type": "GOVERNANCE_VOTE",
                "transactionHash": "transactionHash",
                "transactionIndex": 1,
                "audience": {
                    "policies": ["policyId"],
                    "stakepools": ["stakepool"],
                    "dreps": ["drep"],
                    "global": ["GOVERNANCE_CARDANO"]
                }
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )
    }

    @Test
    fun `serialization does include metadata if provided`() {
        val dto = EventNotification(
            type = EventNotificationType.GOVERNANCE_VOTE,
            transactionHash = "transactionHash",
            transactionIndex = 1,
            audience = Audience(),
            metadata = mapOf("key" to "value"),
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "type": "GOVERNANCE_VOTE",
                "transactionHash": "transactionHash",
                "transactionIndex": 1,
                "audience": { },
                "metadata": {
                    "key": "value"
                }
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
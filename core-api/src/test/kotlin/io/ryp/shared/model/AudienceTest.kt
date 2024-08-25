package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class AudienceTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(Audience::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = Audience(
            policies = listOf("policy"),
            stakepools = listOf("poolhash"),
            dreps = listOf("drep"),
            global = listOf(GlobalAnnouncementAudience.GOVERNANCE_CARDANO),
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals("""
            {
                "policies": ["policy"],
                "stakepools": ["poolhash"],
                "dreps": ["drep"],
                "global": ["GOVERNANCE_CARDANO"]
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }
}
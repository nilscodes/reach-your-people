package io.ryp.cardano.model

import io.ryp.cardano.model.stakepools.VrfVerificationKey
import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class VrfVerificationKeyTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(VrfVerificationKey::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = VrfVerificationKey(
            type = "type",
            description = "description",
            cborHex = "cborHex"
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "type": "type",
                "description": "description",
                "cborHex": "cborHex"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
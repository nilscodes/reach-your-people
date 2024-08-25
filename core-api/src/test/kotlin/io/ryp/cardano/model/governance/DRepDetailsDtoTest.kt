package io.ryp.cardano.model.governance

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class DRepDetailsDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(DRepDetailsDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = DRepDetailsDto(
            drepId = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
            drepView = "drep1abc",
            displayName = "HOSKY",
            currentEpoch = 123,
            delegation = 50000000,
            activeUntil = 124,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "drepId": "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
                "drepView": "drep1abc",
                "displayName": "HOSKY",
                "currentEpoch": 123,
                "delegation": 50000000,
                "activeUntil": 124
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
package io.ryp.cardano.model.stakepools

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class StakepoolDetailsDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(StakepoolDetailsDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = StakepoolDetailsDto(
            poolHash = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
            ticker = "VIBRN",
            name = "Vibrant",
            homepage = "https://vibrantnet.io",
            description = "Vibrant Stake Pool"
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "poolHash": "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
                "ticker": "VIBRN",
                "name": "Vibrant",
                "homepage": "https://vibrantnet.io",
                "description": "Vibrant Stake Pool"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
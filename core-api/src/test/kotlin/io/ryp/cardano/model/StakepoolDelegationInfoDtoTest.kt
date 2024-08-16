package io.ryp.cardano.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class StakepoolDelegationInfoDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(StakepoolDelegationInfoDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = StakepoolDelegationInfoDto(
            poolHash = "poolHash",
            amount = 17000000,
            stakeAddress = "stakeAddress",
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "poolHash": "poolHash",
                "amount": 17000000,
                "stakeAddress": "stakeAddress"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
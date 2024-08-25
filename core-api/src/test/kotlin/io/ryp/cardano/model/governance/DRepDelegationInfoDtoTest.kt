package io.ryp.cardano.model.governance

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class DRepDelegationInfoDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(DRepDelegationInfoDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = DRepDelegationInfoDto(
            drepId = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
            amount = 50000000,
            stakeAddress = "stake1abc"
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "drepId": "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
                "amount": 50000000,
                "stakeAddress": "stake1abc"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
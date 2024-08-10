package io.ryp.cardano.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class SnapshotStakeAddressDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(SnapshotStakeAddressDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = SnapshotStakeAddressDto(
            stakeAddress = "stakeAddress",
            snapshotType = SnapshotType.STAKEPOOL
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "stakeAddress": "stakeAddress",
                "snapshotType": "STAKEPOOL"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
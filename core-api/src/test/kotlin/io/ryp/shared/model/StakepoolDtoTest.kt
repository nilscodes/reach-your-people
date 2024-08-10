package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime

internal class StakepoolDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(StakepoolDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val now = OffsetDateTime.parse("2021-08-01T12:00:00Z")
        val dto = StakepoolDto(
            poolHash = "hash",
            verificationNonce = "nonce",
            verificationTime = now,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
              "poolHash": "hash",
              "verificationNonce": "nonce",
              "verificationTime": "2021-08-01T12:00:00Z"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )
    }
}
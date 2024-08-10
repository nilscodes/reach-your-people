package io.ryp.cardano.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime

internal class StakepoolVerificationDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(StakepoolVerificationDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val now = OffsetDateTime.parse("2021-08-01T12:00:00Z")
        val dto = StakepoolVerificationDto(
            nonce = "random64CharacterHexString",
            domain = "config.cip22.domain",
            poolHash = "poolHash",
            vrfVerificationKey = VrfVerificationKey(
                type = "type",
                description = "description",
                cborHex = "cborHex"
            ),
            signature = "hex",
            createTime = now,
            expirationTime = now.plusMinutes(2)
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "nonce": "random64CharacterHexString",
                "domain": "config.cip22.domain",
                "poolHash": "poolHash",
                "vrfVerificationKey": {
                    "type": "type",
                    "description": "description",
                    "cborHex": "cborHex"
                },
                "signature": "hex",
                "createTime": "2021-08-01T12:00:00Z",
                "expirationTime": "2021-08-01T12:02:00Z"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
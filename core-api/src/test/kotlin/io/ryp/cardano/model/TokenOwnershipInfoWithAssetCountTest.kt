package io.ryp.cardano.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class TokenOwnershipInfoWithAssetCountTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(TokenOwnershipInfoWithAssetCount::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = TokenOwnershipInfoWithAssetCount(
            stakeAddress = "stakeAddress",
            policyIdWithOptionalAssetFingerprint = "policyIdWithOptionalAssetFingerprint",
            assetCount = 1,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "stakeAddress": "stakeAddress",
                "policyIdWithOptionalAssetFingerprint": "policyIdWithOptionalAssetFingerprint",
                "assetCount": 1
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
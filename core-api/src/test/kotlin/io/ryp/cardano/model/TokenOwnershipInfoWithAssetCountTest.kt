package io.ryp.cardano.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class TokenOwnershipInfoWithAssetCountTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(TokenOwnershipInfoWithAssetCount::class.java)
            .verify()
    }
}
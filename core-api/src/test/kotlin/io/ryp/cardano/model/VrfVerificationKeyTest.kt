package io.ryp.cardano.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class VrfVerificationKeyTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(VrfVerificationKey::class.java)
            .verify()
    }
}
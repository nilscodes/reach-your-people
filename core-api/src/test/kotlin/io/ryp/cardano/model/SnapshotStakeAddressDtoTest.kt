package io.ryp.cardano.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class SnapshotStakeAddressDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(SnapshotStakeAddressDto::class.java)
            .verify()
    }
}
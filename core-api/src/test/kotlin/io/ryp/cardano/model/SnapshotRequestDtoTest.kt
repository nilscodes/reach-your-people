package io.ryp.cardano.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class SnapshotRequestDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(SnapshotRequestDto::class.java)
            .verify()
    }
}
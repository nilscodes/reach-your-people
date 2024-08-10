package io.ryp.cardano.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class StakepoolDetailsDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(StakepoolDetailsDto::class.java)
            .verify()
    }
}
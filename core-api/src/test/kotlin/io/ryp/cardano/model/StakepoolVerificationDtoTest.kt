package io.ryp.cardano.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class StakepoolVerificationDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(StakepoolVerificationDto::class.java)
            .verify()
    }
}
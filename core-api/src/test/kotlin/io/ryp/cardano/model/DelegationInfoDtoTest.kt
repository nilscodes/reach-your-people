package io.ryp.cardano.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class DelegationInfoDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(DelegationInfoDto::class.java)
            .verify()
    }
}
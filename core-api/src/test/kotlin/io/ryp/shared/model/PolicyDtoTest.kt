package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class PolicyDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(PolicyDto::class.java)
            .verify()
    }
}
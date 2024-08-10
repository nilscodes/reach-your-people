package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class LinkedExternalAccountPartialDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(LinkedExternalAccountPartialDto::class.java)
            .verify()
    }
}
package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class LinkedExternalAccountDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(LinkedExternalAccountDto::class.java)
            .verify()
    }
}
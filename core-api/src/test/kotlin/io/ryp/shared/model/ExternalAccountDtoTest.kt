package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class ExternalAccountDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ExternalAccountDto::class.java)
            .verify()
    }
}
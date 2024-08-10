package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class MessageDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(MessageDto::class.java)
            .verify()
    }
}
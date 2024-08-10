package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class AnnouncementRecipientDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(AnnouncementRecipientDto::class.java)
            .verify()
    }
}
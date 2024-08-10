package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class BasicAnnouncementWithIdDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(BasicAnnouncementWithIdDto::class.java)
            .verify()
    }
}
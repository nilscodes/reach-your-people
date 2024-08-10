package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class AnnouncementJobDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(AnnouncementJobDto::class.java)
            .verify()
    }
}
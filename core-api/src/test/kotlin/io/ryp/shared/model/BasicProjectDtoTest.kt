package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class BasicProjectDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(BasicProjectDto::class.java)
            .verify()
    }
}
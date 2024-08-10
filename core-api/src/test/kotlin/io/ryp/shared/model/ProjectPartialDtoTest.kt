package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class ProjectPartialDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ProjectPartialDto::class.java)
            .verify()
    }
}
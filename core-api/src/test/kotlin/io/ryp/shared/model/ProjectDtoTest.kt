package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class ProjectDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ProjectDto::class.java)
            .verify()
    }
}
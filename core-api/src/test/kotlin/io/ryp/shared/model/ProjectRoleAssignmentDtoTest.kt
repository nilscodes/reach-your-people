package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class ProjectRoleAssignmentDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ProjectRoleAssignmentDto::class.java)
            .verify()
    }
}
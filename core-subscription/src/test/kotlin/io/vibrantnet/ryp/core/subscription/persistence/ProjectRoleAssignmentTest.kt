package io.vibrantnet.ryp.core.subscription.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class ProjectRoleAssignmentTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ProjectRoleAssignment::class.java)
            .verify()
    }
}
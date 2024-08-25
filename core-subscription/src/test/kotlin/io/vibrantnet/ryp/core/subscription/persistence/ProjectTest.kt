package io.vibrantnet.ryp.core.subscription.persistence

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.ryp.shared.model.*
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

internal class ProjectTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(Project::class.java)
            .verify()
    }

    @Test
    fun testToDto() {
        val now = OffsetDateTime.now()
        val project = Project(
            id = 13,
            name = "name",
            logo = "abc",
            url = "def",
            description = "yodel",
            category = ProjectCategory.dRep,
            registrationTime = now,
            tags = mutableSetOf("tag1", "tag2"),
            policies = mutableSetOf(Policy("name", PolicyId("ceb5dedd6cda3f0b4a98919b5d3827e15e324771642b57e0e6aabd57"))),
            stakepools = mutableSetOf(Stakepool("hash", "nonce", now)),
            dreps = mutableSetOf(DRep("drepId", "nonce", now)),
            roles = mutableSetOf(ProjectRoleAssignment(ProjectRole.OWNER, 5)),
        )
        val dto = project.toDto()
        Assertions.assertEquals(
            ProjectDto(
            id = 13,
            name = "name",
            logo = "abc",
            url = "def",
            description = "yodel",
            category = ProjectCategory.dRep,
            registrationTime = now,
            tags = mutableSetOf("tag1", "tag2"),
            policies = mutableSetOf(PolicyDto("name", "ceb5dedd6cda3f0b4a98919b5d3827e15e324771642b57e0e6aabd57")),
            stakepools = mutableSetOf(StakepoolDto("hash", "nonce", now)),
            dreps = mutableSetOf(DRepDto("drepId", "nonce", now)),
            roles = mutableSetOf(ProjectRoleAssignmentDto(ProjectRole.OWNER, 5)),
        ), dto)
    }
}
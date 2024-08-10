package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime
import kotlin.test.assertEquals

internal class BasicProjectDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(BasicProjectDto::class.java)
            .verify()
    }

    @Test
    fun `creating basic project DTO from ProjectDto works`() {
        val now = OffsetDateTime.parse("2022-01-01T00:00:00Z")
        val projectDto = ProjectDto(
            id = 12,
            name = "Test Project",
            description = "This is a test project",
            logo = "",
            url = "https://ryp.io/projects/12",
            category = ProjectCategory.nFT,
            roles = setOf(ProjectRoleAssignmentDto(ProjectRole.OWNER, 12)),
            policies = setOf(
                PolicyDto("Test Policy", "test-policy", now),
                PolicyDto("Test Policy 2", "test-policy-2", null),
            ),
            stakepools = setOf(
                StakepoolDto("test-stakepool", "abc", now),
            ),
            manuallyVerified = now
        )
        val basicProjectDto = BasicProjectDto(projectDto)
        assertEquals(projectDto.id, basicProjectDto.id)
        assertEquals(projectDto.name, basicProjectDto.name)
        assertEquals(projectDto.logo, basicProjectDto.logo)
        assertEquals(projectDto.url, basicProjectDto.url)
    }

    @Test
    fun serializationTest() {
        val dto = BasicProjectDto(
            id = 12,
            name = "Test Project",
            logo = "",
            url = "https://ryp.io/projects/12",
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals("""
            {
              "id": 12,
              "name": "Test Project",
              "logo": "",
              "url": "https://ryp.io/projects/12"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }
}
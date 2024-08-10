package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class ProjectRoleAssignmentDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ProjectRoleAssignmentDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = ProjectRoleAssignmentDto(
            role = ProjectRole.OWNER,
            accountId = 12,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "role": "OWNER",
                "accountId": 12
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )
    }
}
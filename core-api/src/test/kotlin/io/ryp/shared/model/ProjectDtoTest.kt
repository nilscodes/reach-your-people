package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime

internal class ProjectDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ProjectDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val now = OffsetDateTime.parse("2022-01-01T00:00:00Z")
        val dto = ProjectDto(
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
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals("""
            {
              "id": 12,
              "name": "Test Project",
              "description": "This is a test project",
              "logo": "",
              "url": "https://ryp.io/projects/12",
              "category": "NFT",
              "manuallyVerified": "2022-01-01T00:00:00Z",
              "roles": [
                {
                  "role": "OWNER",
                  "accountId": 12
                }
              ],
              "policies": [
                {
                  "name": "Test Policy",
                  "policyId": "test-policy",
                  "manuallyVerified": "2022-01-01T00:00:00Z"
                },
                {
                  "name": "Test Policy 2",
                  "policyId": "test-policy-2",
                  "manuallyVerified": null
                }
              ],
              "stakepools": [
                {
                  "poolHash": "test-stakepool",
                  "verificationNonce": "abc",
                  "verificationTime": "2022-01-01T00:00:00Z"
                }
              ]
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }


}
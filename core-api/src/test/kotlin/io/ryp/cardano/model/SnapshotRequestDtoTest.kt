package io.ryp.cardano.model

import io.ryp.core.createDefaultObjectMapper
import io.ryp.shared.model.AnnouncementJobDto
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.util.*

internal class SnapshotRequestDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(SnapshotRequestDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
        val uuid2 = UUID.fromString("00000000-0000-0000-0000-000000000002")
        val dto = SnapshotRequestDto(
            announcementRequest = AnnouncementJobDto(
                projectId = 12,
                announcementId = uuid1,
                snapshotId = uuid2
            ),
            policyIds = listOf("policyId"),
            stakepools = listOf("stakepool"),
            dreps = listOf("drep"),
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "announcementRequest": {
                    "projectId": 12,
                    "announcementId": "00000000-0000-0000-0000-000000000001",
                    "snapshotId": "00000000-0000-0000-0000-000000000002"
                },
                "policyIds": ["policyId"],
                "stakepools": ["stakepool"],
                "dreps": ["drep"]
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
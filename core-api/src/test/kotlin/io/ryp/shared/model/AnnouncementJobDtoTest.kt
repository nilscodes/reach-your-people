package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.util.*

internal class AnnouncementJobDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(AnnouncementJobDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
        val uuid2 = UUID.fromString("00000000-0000-0000-0000-000000000002")
        val dto = AnnouncementJobDto(
            projectId = 12,
            announcementId = uuid1,
            snapshotId = uuid2,
            global = listOf(GlobalAnnouncementAudience.GOVERNANCE_CARDANO)
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals("""
            {
              "projectId": 12,
              "announcementId": "00000000-0000-0000-0000-000000000001",
              "snapshotId": "00000000-0000-0000-0000-000000000002",
              "global": ["GOVERNANCE_CARDANO"]
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }
}
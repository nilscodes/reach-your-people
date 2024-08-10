package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.util.*

internal class MessageDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(MessageDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val uuid = UUID.fromString("00000000-0000-0000-0000-000000000001")
        val dto = MessageDto(
            referenceId = "123",
            announcement = BasicAnnouncementWithIdDto(
                id = uuid,
                author = 69L,
                title = "Test Announcement",
                content = "This is a test announcement",
                externalLink = "https://test.com",
                link = "https://ryp.io/announcements/12",
                policies = listOf("test-policy"),
                stakepools = listOf("test-stakepool")
            ),
            metadata = "abc",
            project = BasicProjectDto(
                id = 12,
                name = "Test Project",
                logo = "",
                url = "https://ryp.io/projects/12",
            ),
            referenceName = "name",
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
              "referenceId": "123",
              "announcement": {
                "id": "00000000-0000-0000-0000-000000000001",
                "author": 69,
                "title": "Test Announcement",
                "content": "This is a test announcement",
                "externalLink": "https://test.com",
                "link": "https://ryp.io/announcements/12",
                "policies": ["test-policy"],
                "stakepools": ["test-stakepool"]
              },
              "metadata": "abc",
              "project": {
                "id": 12,
                "name": "Test Project",
                "logo": "",
                "url": "https://ryp.io/projects/12"
              },
              "referenceName": "name"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }
}
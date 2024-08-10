package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.util.*

internal class BasicAnnouncementWithIdDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(BasicAnnouncementWithIdDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val uuid = UUID.fromString("00000000-0000-0000-0000-000000000001")
        val dto = BasicAnnouncementWithIdDto(
            id = uuid,
            author = 69L,
            title = "Test Announcement",
            content = "This is a test announcement",
            externalLink = "https://test.com",
            link = "https://ryp.io/announcements/12",
            policies = listOf("test-policy"),
            stakepools = listOf("test-stakepool")
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals("""
            {
              "id": "00000000-0000-0000-0000-000000000001",
              "author": 69,
              "title": "Test Announcement",
              "content": "This is a test announcement",
              "externalLink": "https://test.com",
              "link": "https://ryp.io/announcements/12",
              "policies": ["test-policy"],
              "stakepools": ["test-stakepool"]
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }
}
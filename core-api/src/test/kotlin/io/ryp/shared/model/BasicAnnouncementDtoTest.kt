package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.util.*

internal class BasicAnnouncementDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(BasicAnnouncementDto::class.java)
            .verify()
    }

    @Test
    fun `toBasicAnnouncementWithIdDto call copies all properties`() {
        val id = UUID.randomUUID()
        val link = "https://otherli.nk"
        val dto = BasicAnnouncementDto(
            author = 1,
            type = AnnouncementType.TEST,
            title = "title",
            content = "content",
            externalLink = "https://ryp.io",
            policies = listOf("4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f"),
            stakepools = listOf("be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4"),
            dreps = listOf("drep123"),
            global = listOf(GlobalAnnouncementAudience.GOVERNANCE_CARDANO)
        )

        val result = dto.toBasicAnnouncementWithIdDto(id, link)

        assertEquals(id, result.id)
        assertEquals(dto.type, result.type)
        assertEquals(dto.author, result.author)
        assertEquals(dto.title, result.title)
        assertEquals(dto.content, result.content)
        assertEquals(link, result.link)
        assertEquals(dto.externalLink, result.externalLink)
        assertEquals(dto.policies, result.policies)
        assertEquals(dto.stakepools, result.stakepools)
        assertEquals(dto.dreps, result.dreps)
        assertEquals(dto.global, result.global)
    }

    @Test
    fun serializationTest() {
        val dto = BasicAnnouncementDto(
            author = 69L,
            AnnouncementType.STANDARD,
            title = "Test Announcement",
            content = "This is a test announcement",
            externalLink = "https://ryp.io/announcements/12",
            policies = listOf("test-policy"),
            stakepools = listOf("test-stakepool"),
            dreps = listOf("test-drep"),
            global = listOf(GlobalAnnouncementAudience.GOVERNANCE_CARDANO)
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals("""
            {
              "author": 69,
              "type": "STANDARD",
              "title": "Test Announcement",
              "content": "This is a test announcement",
              "externalLink": "https://ryp.io/announcements/12",
              "policies": ["test-policy"],
              "stakepools": ["test-stakepool"],
              "dreps": ["test-drep"],
              "global": ["GOVERNANCE_CARDANO"]
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }
}
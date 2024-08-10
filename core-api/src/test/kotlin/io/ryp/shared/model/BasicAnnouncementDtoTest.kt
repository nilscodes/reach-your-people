package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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
            stakepools = listOf("be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4")
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
    }
}
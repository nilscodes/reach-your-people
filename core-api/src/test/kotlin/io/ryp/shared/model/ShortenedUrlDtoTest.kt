package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime
import java.util.*

internal class ShortenedUrlDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ShortenedUrlDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val uuid = UUID.fromString("00000000-0000-0000-0000-000000000001")
        val now = OffsetDateTime.parse("2022-01-01T00:00:00Z")
        val dto = ShortenedUrlDto(
            id = uuid.toString(),
            url = "https://ryp.io/projects/12",
            shortcode = "abc",
            createTime = now,
            type = Type.EXTERNAL,
            status = Status.ACTIVE,
            projectId = 12L,
            views = 1,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals("""
            {
              "id": "00000000-0000-0000-0000-000000000001",
              "shortcode": "abc",
              "createTime": "2022-01-01T00:00:00Z",
              "type": "EXTERNAL",
              "status": "ACTIVE",
              "url": "https://ryp.io/projects/12",
              "projectId": 12,
              "views": 1
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }
}
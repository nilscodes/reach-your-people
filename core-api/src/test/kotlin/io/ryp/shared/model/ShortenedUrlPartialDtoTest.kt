package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class ShortenedUrlPartialDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ShortenedUrlPartialDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = ShortenedUrlPartialDto(
            url = "https://ryp.io/projects/12",
            shortcode = "abc",
            type = Type.EXTERNAL,
            status = Status.ACTIVE,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals("""
            {
              "shortcode": "abc",
              "type": "EXTERNAL",
              "status": "ACTIVE",
              "url": "https://ryp.io/projects/12"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }
}
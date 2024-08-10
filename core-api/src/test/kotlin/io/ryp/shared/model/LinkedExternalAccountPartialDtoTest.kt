package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime

internal class LinkedExternalAccountPartialDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(LinkedExternalAccountPartialDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val now = OffsetDateTime.parse("2021-08-01T12:00:00Z")
        val dto = LinkedExternalAccountPartialDto(
            settings = setOf(ExternalAccountSetting.DREP_ANNOUNCEMENTS),
            lastConfirmed = now,
            lastTested = now,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
              "settings": ["DREP_ANNOUNCEMENTS"],
              "lastConfirmed": "2021-08-01T12:00:00Z",
              "lastTested": "2021-08-01T12:00:00Z"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)

    }
}
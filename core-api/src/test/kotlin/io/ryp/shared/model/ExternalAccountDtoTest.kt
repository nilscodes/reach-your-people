package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime

internal class ExternalAccountDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ExternalAccountDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val now = OffsetDateTime.parse("2021-08-01T12:00:00Z")
        val dto = ExternalAccountDto(
            id = 1,
            referenceId = "123",
            referenceName = "name",
            displayName = "display",
            registrationTime = now,
            unsubscribeTime = now,
            type = "discord",
            metadata = "abc"
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals("""
            {
              "id": 1,
              "referenceId": "123",
              "referenceName": "name",
              "displayName": "display",
              "registrationTime": "2021-08-01T12:00:00Z",
              "unsubscribeTime": "2021-08-01T12:00:00Z",
              "type": "discord",
              "metadata": "abc"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }
}
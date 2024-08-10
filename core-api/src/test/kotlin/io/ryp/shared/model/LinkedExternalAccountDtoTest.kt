package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime

internal class LinkedExternalAccountDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(LinkedExternalAccountDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val now = OffsetDateTime.parse("2021-08-01T12:00:00Z")
        val dto = LinkedExternalAccountDto(
            id = 13,
            externalAccount = ExternalAccountDto(
                id = 1,
                referenceId = "123",
                referenceName = "name",
                displayName = "display",
                registrationTime = null,
                unsubscribeTime = null,
                type = "discord",
                metadata = "abc"
            ),
            role = ExternalAccountRole.OWNER,
            linkTime = now,
            settings = setOf(ExternalAccountSetting.DREP_ANNOUNCEMENTS),
            lastConfirmed = now,
            lastTested = now,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals("""
            {
              "id": 13,
              "externalAccount": {
                "id": 1,
                "referenceId": "123",
                "referenceName": "name",
                "displayName": "display",
                "registrationTime": null,
                "unsubscribeTime": null,
                "type": "discord",
                "metadata": "abc"
              },
              "role": "OWNER",
              "linkTime": "2021-08-01T12:00:00Z",
              "settings": ["DREP_ANNOUNCEMENTS"],
              "lastConfirmed": "2021-08-01T12:00:00Z",
              "lastTested": "2021-08-01T12:00:00Z"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }
}
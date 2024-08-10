package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class AnnouncementRecipientDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(AnnouncementRecipientDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = AnnouncementRecipientDto(
            externalAccountId = 1,
            type = "cardano",
            accountId = 69,
            referenceId = "123",
            metadata = "metadata",
            subscriptionStatus = SubscriptionStatus.DEFAULT,
            referenceName = "name"
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals("""
            {
              "externalAccountId": 1,
              "type": "cardano",
              "accountId": 69,
              "referenceId": "123",
              "metadata": "metadata",
              "subscriptionStatus": "Default",
              "referenceName": "name"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)
    }
}
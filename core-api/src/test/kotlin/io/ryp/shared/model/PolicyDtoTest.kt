package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime

internal class PolicyDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(PolicyDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val now = OffsetDateTime.parse("2021-08-01T12:00:00Z")
        val dto = PolicyDto(
            name = "Test Policy",
            policyId = "test-policy",
            manuallyVerified = now
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
              "name": "Test Policy",
              "policyId": "test-policy",
              "manuallyVerified": "2021-08-01T12:00:00Z"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false)

    }
}
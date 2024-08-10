package io.ryp.shared.model.points

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime

internal class PointsClaimPartialDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(PointsClaimPartialDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val now = OffsetDateTime.parse("2021-08-01T12:00:00Z")
        val dto = PointsClaimPartialDto(
            claimed = false,
            expirationTime = now,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "claimed": false,
                "expirationTime": "2021-08-01T12:00:00Z"
                }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
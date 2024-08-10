package io.ryp.shared.model.points

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime

internal class PointsClaimDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(PointsClaimDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val now = OffsetDateTime.parse("2021-08-01T12:00:00Z")
        val dto = PointsClaimDto(
            claimId = "claim",
            category = "signup",
            tokenId = 18,
            accountId = 12,
            points = 100,
            projectId = 119,
            claimed = false,
            claimTime = now,
            createTime = now,
            expirationTime = now,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
              "claimId": "claim",
              "category": "signup",
                "tokenId": 18,
                "accountId": 12,
                "points": 100,
                "projectId": 119,
                "claimed": false,
                "claimTime": "2021-08-01T12:00:00Z",
                "createTime": "2021-08-01T12:00:00Z",
                "expirationTime": "2021-08-01T12:00:00Z"
                }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
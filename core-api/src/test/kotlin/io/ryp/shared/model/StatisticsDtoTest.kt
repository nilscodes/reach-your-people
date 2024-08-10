package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class StatisticsDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(StatisticsDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = StatisticsDto(
            delivered = 1,
            failures = 2,
            views = 3,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "delivered": 1,
                "failures": 2,
                "views": 3
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )
    }
}
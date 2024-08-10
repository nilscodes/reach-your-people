package io.ryp.shared.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.util.*
import kotlin.test.assertEquals

internal class StatisticsUpdateDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(StatisticsUpdateDto::class.java)
            .verify()
    }

    @Test
    fun `create typed statistics update dto from statistics update dto`() {
        val uuid = UUID.randomUUID()
        val statisticsUpdateDto = StatisticsUpdateDto(uuid, StatisticsDto(1, 2, 3))
        val typedStatisticsUpdateDto = statisticsUpdateDto.withType("discord")
        assertEquals(StatisticsUpdateWithTypeDto("discord", uuid, StatisticsDto(1, 2, 3)), typedStatisticsUpdateDto)
    }

    @Test
    fun serializationTest() {
        val dto = StatisticsUpdateDto(
            announcementId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
            statistics = StatisticsDto(1, 2, 3)
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "announcementId": "00000000-0000-0000-0000-000000000001",
                "statistics": {
                    "delivered": 1,
                    "failures": 2,
                    "views": 3
                }
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )
    }

}
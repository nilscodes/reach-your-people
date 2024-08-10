package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
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

}
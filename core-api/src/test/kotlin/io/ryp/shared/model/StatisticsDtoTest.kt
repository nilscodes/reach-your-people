package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class StatisticsDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(StatisticsDto::class.java)
            .verify()
    }
}
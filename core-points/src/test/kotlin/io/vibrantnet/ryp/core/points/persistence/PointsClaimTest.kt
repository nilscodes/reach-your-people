package io.vibrantnet.ryp.core.points.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

class PointsClaimTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(PointsClaim::class.java)
            .verify()
    }
}
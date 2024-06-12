package io.vibrantnet.ryp.core.points.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

class PointsTokenTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(PointsToken::class.java)
            .verify()
    }
}
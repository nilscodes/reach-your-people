package io.ryp.shared.model.points

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class PointsClaimDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(PointsClaimDto::class.java)
            .verify()
    }
}
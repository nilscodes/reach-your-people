package io.ryp.shared.model.points

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class PointsClaimPartialDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(PointsClaimPartialDto::class.java)
            .verify()
    }
}
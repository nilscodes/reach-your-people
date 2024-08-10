package io.ryp.cardano.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class TxOutputSummaryDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(TxOutputSummaryDto::class.java)
            .verify()
    }
}
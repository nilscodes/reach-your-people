package io.ryp.cardano.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class TransactionSummaryDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(TransactionSummaryDto::class.java)
            .verify()
    }
}
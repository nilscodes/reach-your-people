package io.vibrantnet.ryp.core.billing.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class BillTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(Bill::class.java)
            .verify()
    }
}
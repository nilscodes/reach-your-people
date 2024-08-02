package io.vibrantnet.ryp.core.billing.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class OrderTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(Order::class.java)
            .verify()
    }
}
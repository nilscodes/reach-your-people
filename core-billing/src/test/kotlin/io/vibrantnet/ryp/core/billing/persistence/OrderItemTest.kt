package io.vibrantnet.ryp.core.billing.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class OrderItemTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(OrderItem::class.java)
            .verify()
    }
}
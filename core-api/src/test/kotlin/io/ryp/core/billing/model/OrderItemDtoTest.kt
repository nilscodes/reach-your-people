package io.ryp.core.billing.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class OrderItemDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(OrderItemDto::class.java)
            .verify()
    }
}
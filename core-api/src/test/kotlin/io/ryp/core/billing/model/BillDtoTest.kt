package io.ryp.core.billing.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class BillDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(BillDto::class.java)
            .verify()
    }
}
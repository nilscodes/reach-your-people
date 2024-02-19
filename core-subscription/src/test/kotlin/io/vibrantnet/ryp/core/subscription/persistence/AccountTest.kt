package io.vibrantnet.ryp.core.subscription.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class AccountTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(Account::class.java)
            .verify()
    }
}
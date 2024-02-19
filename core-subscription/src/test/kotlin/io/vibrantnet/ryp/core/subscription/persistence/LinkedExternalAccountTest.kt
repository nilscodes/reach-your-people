package io.vibrantnet.ryp.core.subscription.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class LinkedExternalAccountTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(LinkedExternalAccount::class.java)
            .withIgnoredFields("linkTime")
            .verify()
    }
}
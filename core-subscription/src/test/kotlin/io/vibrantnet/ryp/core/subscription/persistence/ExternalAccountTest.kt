package io.vibrantnet.ryp.core.subscription.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class ExternalAccountTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(ExternalAccount::class.java)
            .withIgnoredFields("referenceName", "registrationTime")
            .verify()
    }
}
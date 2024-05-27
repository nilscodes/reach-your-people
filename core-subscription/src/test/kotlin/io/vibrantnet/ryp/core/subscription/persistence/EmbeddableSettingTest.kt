package io.vibrantnet.ryp.core.subscription.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class EmbeddableSettingTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(EmbeddableSetting::class.java)
            .verify()
    }
}
package io.vibrantnet.ryp.core.verification.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class PartialBlockfrostAssetInfoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(PartialBlockfrostAssetInfo::class.java)
            .verify()
    }
}
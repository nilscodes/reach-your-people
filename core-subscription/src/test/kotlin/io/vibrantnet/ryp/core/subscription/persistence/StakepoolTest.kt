package io.vibrantnet.ryp.core.subscription.persistence

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

internal class StakepoolTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(Stakepool::class.java)
            .verify()
    }

    @Test
    fun `toDto should return a StakepoolDto with the same values as the Stakepool`() {
        val stakepool = Stakepool(
            poolHash = "poolHash",
            verificationNonce = "verificationNonce",
            verificationTime = OffsetDateTime.now()
        )

        val stakepoolDto = stakepool.toDto()

        assertEquals(stakepool.poolHash, stakepoolDto.poolHash)
        assertEquals(stakepool.verificationNonce, stakepoolDto.verificationNonce)
        assertEquals(stakepool.verificationTime, stakepoolDto.verificationTime)
    }
}
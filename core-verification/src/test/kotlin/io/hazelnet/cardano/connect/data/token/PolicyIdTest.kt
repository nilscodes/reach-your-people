package io.hazelnet.cardano.connect.data.token

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PolicyIdTest {
    @Test
    fun `test PolicyId accepts valid policy id`() {
        val policyId = PolicyId("684ffa75d83ccd4dfe179bd37fe679e74d33cce181a6f473337df098")
        assertEquals("684ffa75d83ccd4dfe179bd37fe679e74d33cce181a6f473337df098", policyId.policyId)
    }

    @Test
    fun `test PolicyId rejects invalid policy id with too many characters`() {
        assertThrows(IllegalArgumentException::class.java) {
            PolicyId("684ffa75d83ccd4dfe179bd37fe679e74d33cce181a6f473337df0981")
        }
    }

    @Test
    fun `test PolicyId rejects invalid policy id with too few characters`() {
        assertThrows(IllegalArgumentException::class.java) {
            PolicyId("684ffa75d83ccd4dfe179bd37fe679e74d33cce181a6f473337df09")
        }
    }

}

package io.vibrantnet.ryp.core.subscription.persistence

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.ryp.shared.model.PolicyDto
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class PolicyTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(Policy::class.java)
            .verify()
    }

    @Test
    fun testToDto() {
        val policy = Policy("name", PolicyId("ceb5dedd6cda3f0b4a98919b5d3827e15e324771642b57e0e6aabd57"))
        val dto = policy.toDto()
        Assertions.assertEquals(PolicyDto("name", "ceb5dedd6cda3f0b4a98919b5d3827e15e324771642b57e0e6aabd57"), dto)
    }
}
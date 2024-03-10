package io.vibrantnet.ryp.core.subscription.persistence

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.ryp.shared.model.PolicyDto
import jakarta.persistence.Embeddable

@Embeddable
class Policy(
    var name: String,
    var policyId: PolicyId,
) {
    fun toDto() = PolicyDto(
        name = name,
        policyId = policyId.policyId,
    )
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Policy) return false

        return policyId == other.policyId
    }

    override fun hashCode(): Int {
        return policyId.hashCode()
    }

    override fun toString(): String {
        return "Policy(name='$name', policyId=$policyId)"
    }


}
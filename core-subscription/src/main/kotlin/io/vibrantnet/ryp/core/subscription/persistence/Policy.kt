package io.vibrantnet.ryp.core.subscription.persistence

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.ryp.shared.model.PolicyDto
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.time.OffsetDateTime

@Embeddable
class Policy(
    @Column(name = "name")
    var name: String,

    @Column(name = "policy_id")
    var policyId: PolicyId,

    @Column(name = "manually_verified")
    @Temporal(TemporalType.TIMESTAMP)
    var manuallyVerified: OffsetDateTime? = null,
) {
    fun toDto() = PolicyDto(
        name = name,
        policyId = policyId.policyId,
        manuallyVerified = manuallyVerified,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Policy) return false

        if (name != other.name) return false
        if (policyId != other.policyId) return false
        if (manuallyVerified != other.manuallyVerified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + policyId.hashCode()
        result = 31 * result + (manuallyVerified?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Policy(name='$name', policyId=$policyId, manuallyVerified=$manuallyVerified)"
    }
}
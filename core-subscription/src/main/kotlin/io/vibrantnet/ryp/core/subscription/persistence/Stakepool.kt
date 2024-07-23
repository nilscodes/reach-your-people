package io.vibrantnet.ryp.core.subscription.persistence

import io.ryp.shared.model.StakepoolDto
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Stakepool(
    @Column(name = "pool_hash")
    var poolHash: String,

    @Column(name = "verification_nonce")
    var verificationNonce: String,
) {
    fun toDto() = StakepoolDto(
        poolHash = poolHash,
        verificationNonce = verificationNonce,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Stakepool) return false

        if (poolHash != other.poolHash) return false
        if (verificationNonce != other.verificationNonce) return false

        return true
    }

    override fun hashCode(): Int {
        var result = poolHash.hashCode()
        result = 31 * result + verificationNonce.hashCode()
        return result
    }

    override fun toString(): String {
        return "Stakepool(poolHash='$poolHash', verificationNonce=$verificationNonce)"
    }
}
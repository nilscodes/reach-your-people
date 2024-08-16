package io.vibrantnet.ryp.core.subscription.persistence

import io.ryp.shared.model.DRepDto
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.OffsetDateTime

@Embeddable
class DRep(
    @Column(name = "drep_id")
    var drepId: String,

    @Column(name = "verification_nonce")
    var verificationNonce: String,

    @Column(name = "verification_time")
    var verificationTime: OffsetDateTime = OffsetDateTime.now(),
) {
    fun toDto() = DRepDto(
        drepId = drepId,
        verificationNonce = verificationNonce,
        verificationTime = verificationTime,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DRep) return false

        if (drepId != other.drepId) return false
        if (verificationNonce != other.verificationNonce) return false
        if (verificationTime != other.verificationTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = drepId.hashCode()
        result = 31 * result + verificationNonce.hashCode()
        result = 31 * result + verificationTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "DRep(drepId='$drepId', verificationNonce=$verificationNonce, verificationTime=$verificationTime)"
    }
}
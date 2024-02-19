package io.vibrantnet.ryp.core.subscription.persistence

import io.vibrantnet.ryp.core.subscription.model.LinkedExternalAccountDto
import jakarta.persistence.*
import java.time.OffsetDateTime

@Embeddable
class LinkedExternalAccount(
    @Column(name = "external_account_id")
    var externalAccountId: Long,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "link_time", updatable = false)
    var linkTime: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "role")
    var role: LinkedExternalAccountDto.ExternalAccountRole,
) {


    override fun toString(): String {
        return "LinkedExternalAccount(externalAccountId=$externalAccountId, linkTime=$linkTime, role=$role)"
    }

    fun toDto() = LinkedExternalAccountDto(
        externalAccountId = externalAccountId,
        linkTime = linkTime,
        role = role,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LinkedExternalAccount) return false

        if (externalAccountId != other.externalAccountId) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = externalAccountId.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }
}
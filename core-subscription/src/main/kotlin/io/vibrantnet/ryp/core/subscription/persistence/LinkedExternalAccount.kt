package io.vibrantnet.ryp.core.subscription.persistence

import io.vibrantnet.ryp.core.subscription.model.LinkedExternalAccountDto
import jakarta.persistence.*
import java.time.OffsetDateTime

@Embeddable
class LinkedExternalAccount(
    @ManyToOne
    @JoinColumn(name = "external_account_id")
    var externalAccount: ExternalAccount,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "link_time", updatable = false)
    var linkTime: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "role")
    var role: LinkedExternalAccountDto.ExternalAccountRole,
) {


    override fun toString(): String {
        return "LinkedExternalAccount(externalAccountId=${externalAccount.id}, linkTime=$linkTime, role=$role)"
    }

    fun toDto() = LinkedExternalAccountDto(
        externalAccount = externalAccount.toDto(),
        linkTime = linkTime,
        role = role,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LinkedExternalAccount) return false

        if (externalAccount.id != other.externalAccount.id) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = externalAccount.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }


}
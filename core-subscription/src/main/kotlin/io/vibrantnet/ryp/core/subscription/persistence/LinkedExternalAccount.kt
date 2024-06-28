package io.vibrantnet.ryp.core.subscription.persistence

import io.ryp.shared.model.ExternalAccountRole
import io.ryp.shared.model.ExternalAccountSetting
import io.ryp.shared.model.LinkedExternalAccountDto
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "linked_external_accounts")
class LinkedExternalAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="link_id")
    var id: Long? = null,

    @Column(name = "account_id", nullable = false)
    var accountId: Long,

    @ManyToOne
    @JoinColumn(name = "external_account_id")
    var externalAccount: ExternalAccount,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "link_time", updatable = false)
    var linkTime: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "role")
    @Enumerated(EnumType.ORDINAL)
    var role: ExternalAccountRole,

    // Updatable and insertable false because we don't want to update settings via JPA due to the bit string type not being supported
    @Column(name = "settings", columnDefinition = "bit(16)", updatable = false, insertable = false)
    var settings: String = "1111111111111111",

    @Column(name = "last_confirmed")
    @Temporal(TemporalType.TIMESTAMP)
    var lastConfirmed: OffsetDateTime? = null,

    @Column(name = "last_tested")
    @Temporal(TemporalType.TIMESTAMP)
    var lastTested: OffsetDateTime? = null,
) {
    override fun toString(): String {
        return "LinkedExternalAccount(externalAccountId=${externalAccount.id}, accountId=$accountId, linkTime=$linkTime, role=$role, externalAccountSettings=$settings, lastConfirmed=$lastConfirmed, lastTested=$lastTested)"
    }

    fun toDto() = LinkedExternalAccountDto(
        id = id,
        externalAccount = externalAccount.toDto(),
        linkTime = linkTime,
        role = role,
        settings = settingsAsSet(),
        lastConfirmed = lastConfirmed,
        lastTested = lastTested,
    )

    fun settingsAsSet(): Set<ExternalAccountSetting> {
        val enumSet = EnumSet.noneOf(ExternalAccountSetting::class.java)
        for (i in 0..<ExternalAccountSetting.entries.size) {
            if (settings[15 - i] == '1') {
                enumSet.add(ExternalAccountSetting.entries[i])
            }
        }
        return enumSet
    }

    fun settingsFromSet(settings: Set<ExternalAccountSetting>) {
        val bitArray = CharArray(16) { '1' }
        for (i in 0..<ExternalAccountSetting.entries.size) {
            bitArray[15 - i] = if(settings.contains(ExternalAccountSetting.entries[i])) '1' else '0'
        }
        this.settings = String(bitArray)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LinkedExternalAccount) return false

        if (accountId != other.accountId) return false
        if (externalAccount.id != other.externalAccount.id) return false
        if (role != other.role) return false
        if (settings != other.settings) return false
        if (lastConfirmed != other.lastConfirmed) return false
        if (lastTested != other.lastTested) return false

        return true
    }

    override fun hashCode(): Int {
        var result = externalAccount.hashCode()
        result = 31 * result + accountId.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + settings.hashCode()
        result = 31 * result + (lastConfirmed?.hashCode() ?: 0)
        result = 31 * result + (lastTested?.hashCode() ?: 0)
        return result
    }

}
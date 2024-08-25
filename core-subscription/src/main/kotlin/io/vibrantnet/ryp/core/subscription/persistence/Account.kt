package io.vibrantnet.ryp.core.subscription.persistence

import io.vibrantnet.ryp.core.subscription.model.AccountDto
import io.vibrantnet.ryp.core.subscription.model.CardanoSetting
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "accounts")
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="account_id")
    var id: Long? = null,

    @Column(name = "display_name")
    var displayName: String?,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    var createTime: OffsetDateTime = OffsetDateTime.now(),

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "account_id")
    var linkedExternalAccounts: MutableSet<LinkedExternalAccount> = mutableSetOf(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "subscriptions", joinColumns = [JoinColumn(name = "account_id")])
    var subscriptions: MutableSet<Subscription> = mutableSetOf(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "account_settings", joinColumns = [JoinColumn(name = "account_id")])
    var settings: MutableSet<EmbeddableSetting> = mutableSetOf(),

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "premium_until")
    var premiumUntil: OffsetDateTime? = null,

    // Updatable and insertable false because we don't want to update settings via JPA due to the bit string type not being supported
    @Column(name = "cardano_settings", columnDefinition = "bit(16)", updatable = false, insertable = false)
    var cardanoSettings: String = "1111111111111111",
) {

    fun toDto() = AccountDto(
        id = id,
        displayName = displayName!!,
        createTime = createTime,
        premiumUntil = premiumUntil,
        cardanoSettings = cardanoSettingsAsSet(),
    )

    fun cardanoSettingsAsSet(): Set<CardanoSetting> {
        return CardanoSettingsUtil.settingsAsSet(cardanoSettings)
    }

    fun cardanoSettingsFromSet(cardanoSettings: Set<CardanoSetting>) {
        this.cardanoSettings = CardanoSettingsUtil.settingsFromSet(cardanoSettings)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Account) return false

        if (displayName != other.displayName) return false
        if (createTime != other.createTime) return false
        if (linkedExternalAccounts != other.linkedExternalAccounts) return false
        if (subscriptions != other.subscriptions) return false
        if (settings != other.settings) return false
        if (premiumUntil != other.premiumUntil) return false
        if (cardanoSettings != other.cardanoSettings) return false

        return true
    }

    override fun hashCode(): Int {
        var result = displayName?.hashCode() ?: 0
        result = 31 * result + createTime.hashCode()
        result = 31 * result + linkedExternalAccounts.hashCode()
        result = 31 * result + subscriptions.hashCode()
        result = 31 * result + settings.hashCode()
        result = 31 * result + (premiumUntil?.hashCode() ?: 0)
        result = 31 * result + cardanoSettings.hashCode()
        return result
    }

    override fun toString(): String {
        return "Account(id=$id, displayName=$displayName, createTime=$createTime, premiumUntil=$premiumUntil, cardanoSettings=$cardanoSettings)"
    }

}

object CardanoSettingsUtil {
    fun settingsAsSet(settings: String): Set<CardanoSetting> {
        val enumSet = EnumSet.noneOf(CardanoSetting::class.java)
        for (i in 0 until CardanoSetting.entries.size) {
            if (settings[15 - i] == '1') {
                enumSet.add(CardanoSetting.entries[i])
            }
        }
        return enumSet
    }

    fun settingsFromSet(settings: Set<CardanoSetting>, base: Char = '1'): String {
        val bitArray = CharArray(16) { base }
        for (i in 0 until CardanoSetting.entries.size) {
            bitArray[15 - i] = if (settings.contains(CardanoSetting.entries[i])) '1' else '0'
        }
        return String(bitArray)
    }
}
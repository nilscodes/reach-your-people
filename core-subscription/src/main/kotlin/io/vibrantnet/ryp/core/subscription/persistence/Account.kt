package io.vibrantnet.ryp.core.subscription.persistence

import io.vibrantnet.ryp.core.subscription.model.AccountDto
import jakarta.persistence.*
import java.time.OffsetDateTime

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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "linked_external_accounts", joinColumns = [JoinColumn(name = "account_id")])
    var linkedExternalAccounts: MutableSet<LinkedExternalAccount> = mutableSetOf(),

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "subscriptions", joinColumns = [JoinColumn(name = "account_id")])
    var subscriptions: MutableSet<Subscription> = mutableSetOf(),
) {



    fun toDto() = AccountDto(
        id = id,
        displayName = displayName!!,
        createTime = createTime,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Account) return false

        if (displayName != other.displayName) return false
        if (createTime != other.createTime) return false
        if (linkedExternalAccounts != other.linkedExternalAccounts) return false
        if (subscriptions != other.subscriptions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = displayName?.hashCode() ?: 0
        result = 31 * result + createTime.hashCode()
        result = 31 * result + linkedExternalAccounts.hashCode()
        result = 31 * result + subscriptions.hashCode()
        return result
    }

    override fun toString(): String {
        return "Account(id=$id, displayName=$displayName, createTime=$createTime, linkedExternalAccounts=$linkedExternalAccounts, subscriptions=$subscriptions)"
    }

}

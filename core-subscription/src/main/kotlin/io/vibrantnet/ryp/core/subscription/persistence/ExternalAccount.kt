package io.vibrantnet.ryp.core.subscription.persistence

import io.ryp.shared.model.ExternalAccountDto
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "external_accounts")
class ExternalAccount(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "external_account_id")
        var id: Long? = 0,

        @Column(name = "external_reference_id")
        var referenceId: String,

        @Column(name = "external_reference_name")
        var referenceName: String?,

        @Column(name = "display_name")
        var displayName: String?,

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "registration_time", updatable = false)
        var registrationTime: OffsetDateTime = OffsetDateTime.now(),

        @Column(name = "account_type")
        var type: String,
) {


    fun toDto() = ExternalAccountDto(
        id = id,
        referenceId = referenceId,
        referenceName = referenceName,
        displayName = displayName,
        registrationTime = registrationTime,
        type = type,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExternalAccount) return false

        if (referenceId != other.referenceId) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = referenceId.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "ExternalAccount(id=$id, referenceId='$referenceId', referenceName=$referenceName, displayName=$displayName, registrationTime=$registrationTime, type='$type')"
    }

}

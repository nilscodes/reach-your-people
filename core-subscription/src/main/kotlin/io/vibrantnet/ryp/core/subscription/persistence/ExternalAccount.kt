package io.vibrantnet.ryp.core.subscription.persistence

import io.ryp.shared.model.ExternalAccountDto
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

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

        @Basic(fetch = FetchType.EAGER)
        @Column(name = "metadata", columnDefinition = "bytea")
        var metadata: ByteArray? = null,
) {


    fun toDto() = ExternalAccountDto(
        id = id,
        referenceId = referenceId,
        referenceName = referenceName,
        displayName = displayName,
        registrationTime = registrationTime,
        type = type,
        // Not ideal that we do this for all metadata-containing external accounts, might be worth revisiting
        metadata = metadata.let { metadata ->
            if (metadata != null) {
                Base64.getEncoder().encodeToString(metadata)
            } else {
                null
            }
        },
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
        return "ExternalAccount(id=$id, referenceId='$referenceId', referenceName=$referenceName, displayName=$displayName, registrationTime=$registrationTime, type='$type', metadata=${metadata?.size} bytes)"
    }

}

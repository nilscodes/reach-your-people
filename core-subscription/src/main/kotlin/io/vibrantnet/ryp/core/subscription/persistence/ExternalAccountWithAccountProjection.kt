package io.vibrantnet.ryp.core.subscription.persistence

import java.time.OffsetDateTime

interface ExternalAccountWithAccountProjection {
    val id: Long
    val referenceId: String
    val referenceName: String?
    val displayName: String?
    val registrationTime: OffsetDateTime
    val type: String
    val metadata: ByteArray?
    val accountId: Long
}
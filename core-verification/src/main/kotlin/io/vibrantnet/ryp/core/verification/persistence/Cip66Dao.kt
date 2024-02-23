package io.vibrantnet.ryp.core.verification.persistence

import io.vibrantnet.ryp.core.verification.model.Cip66PayloadDto

const val CIP66_METADATA_KEY = 725
const val DEFAULT_CIP66_VERSION = "1.0"

fun interface Cip66Dao {
    fun getCip66Payload(policyId: String): Cip66PayloadDto
}
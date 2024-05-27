package io.vibrantnet.ryp.vibrant.model

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*

data class ExternalAccountDto @JsonCreator constructor(
    val id: Long,
    val referenceId: String,
    val referenceName: String?,
    val registrationTime: Date?,
    val type: ExternalAccountType,
    val account: Long? = 0,
)

enum class ExternalAccountType {
    DISCORD
}
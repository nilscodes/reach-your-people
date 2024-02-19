package io.vibrantnet.ryp.core.subscription.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.OffsetDateTime

data class LinkedExternalAccountDto @JsonCreator constructor(

    @JsonProperty("externalAccountId", required = true)
    @field:Min(1)
    val externalAccountId: Long,

    @JsonProperty("role")
    @field:NotNull
    val role: ExternalAccountRole,

    @JsonProperty("linkTime")
    val linkTime: OffsetDateTime? = null
) {

    enum class ExternalAccountRole {
        OWNER,
        ADMIN,
        PUBLISHER,
        SUBSCRIBER,
    }

}


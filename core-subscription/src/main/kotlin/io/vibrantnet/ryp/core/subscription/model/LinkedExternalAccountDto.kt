package io.vibrantnet.ryp.core.subscription.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import java.time.OffsetDateTime

data class LinkedExternalAccountDto @JsonCreator constructor(

    @JsonProperty("externalAccount", required = true)
    @field:Valid
    val externalAccount: ExternalAccountDto,

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


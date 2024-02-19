package io.vibrantnet.ryp.core.subscription.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime

data class ExternalAccountDto @JsonCreator constructor(
    @JsonProperty("id")
    val id: Long? = null,

    @JsonProperty("referenceId")
    @field:NotNull
    @field:Size(min = 1, max = 200)
    val referenceId: String,

    @JsonProperty("referenceName")
    @field:Size(max = 200)
    val referenceName: String? = null,

    @JsonProperty("registrationTime")
    val registrationTime: OffsetDateTime? = null,

    @JsonProperty("type")
    @field:NotNull
    @field:Size(min = 1, max = 64)
    val type: String,
)
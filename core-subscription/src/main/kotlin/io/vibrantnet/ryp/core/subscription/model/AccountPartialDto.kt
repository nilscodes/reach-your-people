package io.vibrantnet.ryp.core.subscription.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size

data class AccountPartialDto @JsonCreator constructor(
    @JsonProperty("displayName")
    @field:Size(min = 1, max = 200)
    val displayName: String? = null
)

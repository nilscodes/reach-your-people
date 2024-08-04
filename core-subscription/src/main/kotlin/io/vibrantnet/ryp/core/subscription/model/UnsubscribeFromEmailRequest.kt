package io.vibrantnet.ryp.core.subscription.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email

data class UnsubscribeFromEmailRequest(
    @field:Email
    @JsonProperty("email", required = true)
    val email: String
)

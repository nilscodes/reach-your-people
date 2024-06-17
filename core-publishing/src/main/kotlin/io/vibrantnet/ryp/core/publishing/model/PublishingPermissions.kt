package io.vibrantnet.ryp.core.publishing.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid

data class PublishingPermissions @JsonCreator constructor(
    @field:Valid
    @JsonProperty("policies")
    val policies: List<PolicyPublishingPermission>,

    @JsonProperty("accountId")
    val accountId: Long,
)


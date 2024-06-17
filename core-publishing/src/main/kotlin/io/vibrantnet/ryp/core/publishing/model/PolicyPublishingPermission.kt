package io.vibrantnet.ryp.core.publishing.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Pattern

data class PolicyPublishingPermission @JsonCreator constructor(
    @field:Pattern(regexp = "^[A-Za-z0-9]{56}$")
    @JsonProperty("policyId")
    val policyId: String,

    @get:JsonProperty("permission")
    val permission: PublishingPermissionStatus,
)

enum class PublishingPermissionStatus {
    PUBLISHING_MANUAL,
    PUBLISHING_CIP66,
    PUBLISHING_NOT_GRANTED;

    fun isPublishingAllowed(): Boolean {
        return this == PUBLISHING_MANUAL || this == PUBLISHING_CIP66
    }
}
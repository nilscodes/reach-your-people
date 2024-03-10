package io.vibrantnet.ryp.core.subscription.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class NewSubscriptionDto @JsonCreator constructor(
    @JsonProperty("status", required = true) val status: SubscriptionStatus,
)

data class ProjectSubscriptionDto @JsonCreator constructor(
    @JsonProperty("projectId", required = true) val projectId: Long,
    @JsonProperty("status", required = true) val status: SubscriptionStatus
)

enum class SubscriptionStatus {
    SUBSCRIBED,
    BLOCKED
}
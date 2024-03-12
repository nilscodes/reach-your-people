package io.vibrantnet.ryp.core.subscription.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class NewSubscriptionDto @JsonCreator constructor(
    @JsonProperty("status", required = true) val status: SubscriptionStatus,
)

data class ProjectSubscriptionDto @JsonCreator constructor(
    @JsonProperty("projectId", required = true) val projectId: Long,
    @JsonProperty("defaultStatus", required = true) val defaultStatus: DefaultSubscriptionStatus = DefaultSubscriptionStatus.UNSUBSCRIBED,
    @JsonProperty("currentStatus", required = true) val currentStatus: SubscriptionStatus,
)

enum class SubscriptionStatus {
    @JsonProperty("Default") DEFAULT,
    @JsonProperty("Subscribed") SUBSCRIBED,
    @JsonProperty("Unsubscribed") BLOCKED,
    @JsonProperty("Muted") MUTED,
}

enum class DefaultSubscriptionStatus {
    @JsonProperty("Subscribed") SUBSCRIBED,
    @JsonProperty("Unsubscribed") UNSUBSCRIBED,
}
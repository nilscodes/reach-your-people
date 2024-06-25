package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonProperty

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
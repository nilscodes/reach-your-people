package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class AnnouncementRecipientDto @JsonCreator constructor(
    @JsonProperty("externalAccountId")
    val externalAccountId: Long,

    @JsonProperty("type")
    val type: String,

    @JsonProperty("accountId")
    val accountId: Long,

    @JsonProperty("referenceId")
    val referenceId: String,

    @JsonProperty("metadata")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val metadata: String? = null,

    @JsonProperty("subscriptionStatus")
    val subscriptionStatus: SubscriptionStatus,

    @JsonProperty("referenceName")
    val referenceName: String? = null,
)
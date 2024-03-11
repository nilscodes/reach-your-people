package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class AnnouncementRecipientDto @JsonCreator constructor(
    @JsonProperty("type") val type: String,
    @JsonProperty("referenceId") val referenceId: String
)
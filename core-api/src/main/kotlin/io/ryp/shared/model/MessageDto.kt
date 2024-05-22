package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class MessageDto @JsonCreator constructor(
    @JsonProperty("referenceId")
    val referenceId: String,

    @JsonProperty("announcement")
    val announcement: BasicAnnouncementWithIdDto,

    @JsonProperty("metadata")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val metadata: String? = null,
)

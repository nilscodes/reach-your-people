package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class BasicAnnouncementDto @JsonCreator constructor(
    @JsonProperty("author", required = true) val author: Long,

    @JsonProperty("title", required = true) val title: String,

    @JsonProperty("content", required = true) val content: String,

    @JsonProperty("link") val link: String? = null
)


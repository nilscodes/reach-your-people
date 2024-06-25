package io.vibrantnet.ryp.core.publishing.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

data class Audience @JsonCreator constructor(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val policies: List<String>,
)
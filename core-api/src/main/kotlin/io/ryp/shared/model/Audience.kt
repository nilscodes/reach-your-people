package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

data class Audience @JsonCreator constructor(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val policies: List<String> = emptyList(),

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val stakepools: List<String> = emptyList(),

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val dreps: List<String> = emptyList()
)
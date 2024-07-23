package io.ryp.cardano.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class StakepoolDetailsDto @JsonCreator constructor(
    @JsonProperty("poolHash", required = true)
    val poolHash: String,

    @JsonProperty("ticker", required = true)
    val ticker: String,

    @JsonProperty("name", required = true)
    val name: String,

    @JsonProperty("homepage", required = true)
    val homepage: String,

    @JsonProperty("description", required = true)
    val description: String,
)

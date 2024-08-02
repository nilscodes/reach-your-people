package io.ryp.cardano.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TxOutputSummaryDto(
    @JsonProperty("address", required = true)
    val address: String,

    @JsonProperty("lovelace", required = true)
    val lovelace: Long,
)

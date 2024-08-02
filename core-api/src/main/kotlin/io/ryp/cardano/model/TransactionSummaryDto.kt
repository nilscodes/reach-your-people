package io.ryp.cardano.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import java.time.OffsetDateTime

/**
 *
 * @param transactionHash The transaction hash in hex format
 * @param outputs
 */
data class TransactionSummaryDto(
    @field:Pattern(regexp="^[a-zA-Z0-9]{64}$")
    @JsonProperty("transactionHash", required = true)
    val transactionHash: String,

    @JsonProperty("transactionTime", required = true)
    val transactionTime: OffsetDateTime,

    @field:Valid
    @JsonProperty("outputs", required = true)
    val outputs: List<TxOutputSummaryDto>
)


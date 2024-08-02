package io.ryp.core.billing.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min

/**
 *
 * @param type The type of item
 * @param amount The amount of the item that was ordered
 */
data class OrderItemDto(

    @JsonProperty("type", required = true)
    val type: String,

    @JsonProperty("amount")
    @field:Min(1)
    val amount: Long,
)


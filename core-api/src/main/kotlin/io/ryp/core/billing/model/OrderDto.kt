package io.ryp.core.billing.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid

data class OrderDto(
    @JsonProperty("id")
    val id: Int? = null,

    @field:Valid
    @JsonProperty("items", required = true)
    val items: List<OrderItemDto>
)

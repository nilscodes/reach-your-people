package io.ryp.core.billing.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import java.time.OffsetDateTime

/**
 *
 * @param channel The payment channel for this bill.
 * @param amountRequested The amount requested in the currency denoted in the respective property
 * @param currencyId
 * @param transactionId The identifier by which the payment transaction can be looked up. Depends on the channel type.
 * @param order
 * @param id
 * @param amountReceived The amount received in the currency denoted in the respective property
 */
data class BillDto(
    @JsonProperty("id")
    val id: Int? = null,

    @JsonProperty("accountId")
    val accountId: Long = 0,

    @JsonProperty("channel", required = true)
    @field:NotBlank
    val channel: String,

    @JsonProperty("currencyId", required = true)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    val currency: Currency,

    @JsonProperty("amountRequested", required = true)
    val amountRequested: Long,

    @field:Valid
    @JsonProperty("order", required = true)
    val order: OrderDto,

    @JsonProperty("createTime")
    val createTime: OffsetDateTime? = null,

    @JsonProperty("transactionId")
    val transactionId: String? = null,

    @JsonProperty("amountReceived")
    val amountReceived: Long? = null,

    @JsonProperty("paymentProcessedTime")
    val paymentProcessedTime: OffsetDateTime? = null,
)

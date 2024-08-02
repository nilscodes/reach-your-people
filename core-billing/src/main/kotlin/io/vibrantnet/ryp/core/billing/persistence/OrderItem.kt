package io.vibrantnet.ryp.core.billing.persistence

import io.ryp.core.billing.model.OrderItemDto
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class OrderItem(
    @Column(name = "type")
    var type: String,

    @Column(name = "amount")
    var amount: Long,
) {
    fun toDto() = OrderItemDto(
        type = type,
        amount = amount,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderItem) return false

        if (type != other.type) return false
        if (amount != other.amount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + amount.hashCode()
        return result
    }

    override fun toString(): String {
        return "OrderItem(type='$type', amount=$amount)"
    }

}
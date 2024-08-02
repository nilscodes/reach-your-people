package io.vibrantnet.ryp.core.billing.persistence

import io.ryp.core.billing.model.OrderDto
import jakarta.persistence.*

@Entity
@Table(name = "orders")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    var id: Int? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = [JoinColumn(name = "order_id")])
    val items: MutableList<OrderItem>,
) {
    fun toDto() = OrderDto(
        id = id,
        items = items.map { it.toDto() },
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Order) return false

        return items == other.items
    }

    override fun hashCode(): Int {
        return items.hashCode()
    }

    override fun toString(): String {
        return "Order(items=$items)"
    }

}
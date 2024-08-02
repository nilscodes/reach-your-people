package io.vibrantnet.ryp.core.billing.persistence

import io.ryp.core.billing.model.BillDto
import io.ryp.core.billing.model.Currency
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "bills")
class Bill(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int? = null,

    @Column(name = "account_id")
    var accountId: Long,

    @Column(name = "channel")
    var channel: String,

    @Column(name = "currency_id")
    @Enumerated(EnumType.ORDINAL)
    var currency: Currency,

    @Column(name = "amount_requested")
    var amountRequested: Long,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    var order: Order,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    var createTime: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "amount_received")
    var amountReceived: Long? = null,

    @Column(name = "transaction_id")
    var transactionId: String? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payment_processed_time")
    var paymentProcessedTime: OffsetDateTime? = null,
) {
    fun toDto() = BillDto(
        id = id,
        accountId = accountId,
        channel = channel,
        currency = currency,
        amountRequested = amountRequested,
        order = order.toDto(),
        createTime = createTime,
        amountReceived = amountReceived,
        transactionId = transactionId,
        paymentProcessedTime = paymentProcessedTime,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Bill) return false

        if (accountId != other.accountId) return false
        if (channel != other.channel) return false
        if (currency != other.currency) return false
        if (amountRequested != other.amountRequested) return false
        if (order != other.order) return false
        if (createTime != other.createTime) return false
        if (amountReceived != other.amountReceived) return false
        if (transactionId != other.transactionId) return false
        if (paymentProcessedTime != other.paymentProcessedTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = accountId.hashCode()
        result = 31 * result + channel.hashCode()
        result = 31 * result + currency.hashCode()
        result = 31 * result + amountRequested.hashCode()
        result = 31 * result + order.hashCode()
        result = 31 * result + createTime.hashCode()
        result = 31 * result + (amountReceived?.hashCode() ?: 0)
        result = 31 * result + (transactionId?.hashCode() ?: 0)
        result = 31 * result + (paymentProcessedTime?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Bill(id=$id, accountId=$accountId, channel='$channel', currency=$currency, amountRequested=$amountRequested, order=$order, createTime=$createTime, amountReceived=$amountReceived, transactionId=$transactionId, paymentProcessedTime=$paymentProcessedTime)"
    }

}
package io.vibrantnet.ryp.core.subscription.service

import io.ryp.core.billing.model.BillDto
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.temporal.ChronoUnit

@Service
class OrderServiceVibrant(
    private val accountsApiService: AccountsApiService,
) {
    @RabbitListener(queues = ["orderconfirmations"])
    fun receiveOrderConfirmation(confirmedBill: BillDto) {
        val premiumDurationMonths = confirmedBill.order.items.firstOrNull { it.type == "premium" }?.amount ?: 0
        if (premiumDurationMonths > 0) {
            accountsApiService.extendPremium(
                confirmedBill.accountId,
                Duration.of(premiumDurationMonths * 730, ChronoUnit.HOURS) // Cannot use months, because it is an estimated duration, so we use 730 hours which is equivalent to 30.4 days
            )
                .subscribe()
        }
    }
}
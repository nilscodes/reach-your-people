package io.vibrantnet.ryp.core.subscription.service

import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import io.ryp.core.billing.model.BillDto
import io.ryp.core.billing.model.Currency
import io.ryp.core.billing.model.OrderDto
import io.ryp.core.billing.model.OrderItemDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.temporal.ChronoUnit

internal class OrderServiceVibrantTest {
    private val accountsApiService = mockk<AccountsApiService>(relaxed = true)
    private val service = OrderServiceVibrant(accountsApiService)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `if premium account is in confirmed order, extend premium`() {
        val confirmedBill = BillDto(
            accountId = 1,
            order = OrderDto(
                items = listOf(
                    OrderItemDto(type = "premium", amount = 1)
                )
            ),
            amountRequested = 1000000L,
            currency = Currency.LOVELACE_ADA,
            channel = "cardano"
        )

        service.receiveOrderConfirmation(confirmedBill)

        verify(exactly = 1) { accountsApiService.extendPremium(1, Duration.of(730, ChronoUnit.HOURS)) }
    }

    @Test
    fun `if no premium account is in confirmed order, nothing happens`() {
        val confirmedBill = BillDto(
            accountId = 1,
            order = OrderDto(
                items = listOf(
                    OrderItemDto(type = "basic", amount = 1)
                )
            ),
            amountRequested = 1000000L,
            currency = Currency.LOVELACE_ADA,
            channel = "cardano"
        )

        service.receiveOrderConfirmation(confirmedBill)

        verify { accountsApiService wasNot Called }
    }

    @Test
    fun `if premium is in order but without amount, nothing happens`() {
        val confirmedBill = BillDto(
            accountId = 1,
            order = OrderDto(
                items = listOf(
                    OrderItemDto(type = "premium", amount = 0)
                )
            ),
            amountRequested = 1000000L,
            currency = Currency.LOVELACE_ADA,
            channel = "cardano"
        )

        service.receiveOrderConfirmation(confirmedBill)

        verify { accountsApiService wasNot Called }
    }
}
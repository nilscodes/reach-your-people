package io.vibrantnet.ryp.core.billing.service

import io.mockk.*
import io.ryp.cardano.model.TransactionSummaryDto
import io.ryp.cardano.model.TxOutputSummaryDto
import io.ryp.core.billing.model.BillDto
import io.ryp.core.billing.model.Currency
import io.ryp.core.billing.model.OrderDto
import io.ryp.core.billing.model.OrderItemDto
import io.vibrantnet.ryp.core.billing.CoreBillingConfiguration
import io.vibrantnet.ryp.core.billing.PaymentConfiguration
import io.vibrantnet.ryp.core.billing.persistence.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime

internal class BillingApiServiceVibrantTest {
    private val config = CoreBillingConfiguration("", PaymentConfiguration(receiveAddress = "addr1itsyou"))
    private val verifyService = mockk<VerifyService>()
    private val billRepository = mockk<BillRepository>()
    private val orderRepository = mockk<OrderRepository>()
    private val rabbitTemplate = mockk<RabbitTemplate>()
    private val service = BillingApiServiceVibrant(config, verifyService, billRepository, orderRepository, rabbitTemplate)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `createBill should save order and bill`() {
        val billDto = makeBillDto()
        every { orderRepository.save(any()) } answers {
            firstArg<Order>().also { it.id = 123 }
        }
        every { billRepository.save(match { it.order.id == 123 }) } answers {
            firstArg<Bill>().also { it.id = 209 }
        }

        val result = service.createBill(billDto.accountId, billDto)

        StepVerifier.create(result)
            .expectNextMatches {
                it.id == 209
                        && it.accountId == 12L
                        && it.channel == "cardano"
                        && it.currency == Currency.LOVELACE_ADA
                        && it.order.items == listOf(OrderItemDto("stuff", 17))
                        && it.amountRequested == 25000000L
                        && it.amountReceived == null
                        && it.paymentProcessedTime == null
                        && it.createTime != null

            }
            .verifyComplete()
    }

    @Test
    fun `creating bill with preset received amount and processing time ignores those values`() {
        val billDto = makeBillDto(123456L, OffsetDateTime.now())
        every { orderRepository.save(any()) } answers {
            firstArg<Order>().also { it.id = 123 }
        }
        every { billRepository.save(match { it.order.id == 123 }) } answers {
            firstArg<Bill>().also { it.id = 209 }
        }

        val result = service.createBill(billDto.accountId, billDto)

        StepVerifier.create(result)
            .expectNextMatches {
                it.id == 209
                        && it.accountId == 12L
                        && it.channel == "cardano"
                        && it.currency == Currency.LOVELACE_ADA
                        && it.order.items == listOf(OrderItemDto("stuff", 17))
                        && it.amountRequested == 25000000L
                        && it.amountReceived == null
                        && it.paymentProcessedTime == null
                        && it.createTime != null

            }
            .verifyComplete()
    }

    @Test
    fun `getting bills for an account works`() {
        val billDto = makeBillDto()
        val now = OffsetDateTime.now()
        every { billRepository.findAllByAccountId(billDto.accountId) } answers {
            listOf(billFromDto(billDto, now))
        }

        val result = service.getBillsForAccount(billDto.accountId)

        StepVerifier.create(result)
            .expectNext(billDto.copy(createTime = now))
            .verifyComplete()
    }

    private fun billFromDto(
        billDto: BillDto,
        now: OffsetDateTime
    ) = Bill(
        id = billDto.id,
        accountId = billDto.accountId,
        channel = billDto.channel,
        currency = billDto.currency,
        amountRequested = billDto.amountRequested,
        order = Order(
            id = billDto.order.id,
            items = billDto.order.items.map { OrderItem(it.type, it.amount) }.toMutableList(),
        ),
        createTime = now,
        transactionId = billDto.transactionId,
    )

    @Test
    fun `payment processing works and sends message if full payment is made`() {
        val billDto = makeBillDto()
        val now = OffsetDateTime.now()
        every { billRepository.findAllByChannelAndAmountReceivedIsNullAndTransactionIdIsNotNull("cardano") } answers {
            listOf(billFromDto(billDto, now).also { it.transactionId = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b" })
        }
        every { billRepository.save(any()) } returnsArgument 0
        every { rabbitTemplate.convertAndSend("orderconfirmations", any<BillDto>()) } just Runs
        every { verifyService.getTransactionSummary("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b") } answers {
                Mono.just(
                    TransactionSummaryDto(
                        "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
                        now,
                        listOf(
                            TxOutputSummaryDto("addr1itsyou", 26000000L)
                        )
                    )
                )
        }

        service.confirmCardanoBills()

        verify(exactly = 1) { billRepository.save(match {
            it.amountReceived == 26000000L && it.paymentProcessedTime != null
        }) }
        verify(exactly = 1) { rabbitTemplate.convertAndSend("orderconfirmations", any<BillDto>()) }
    }

    @Test
    fun `payment is rejected if transaction is notably older than the order`() {
        val billDto = makeBillDto()
        val now = OffsetDateTime.now()
        every { billRepository.findAllByChannelAndAmountReceivedIsNullAndTransactionIdIsNotNull("cardano") } answers {
            listOf(billFromDto(billDto, now).also { it.transactionId = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b" })
        }
        every { billRepository.save(any()) } returnsArgument 0
        every { verifyService.getTransactionSummary("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b") } answers {
            Mono.just(
                TransactionSummaryDto(
                    "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
                    OffsetDateTime.parse("2021-01-01T00:00:00Z"),
                    listOf(
                        TxOutputSummaryDto("addr1itsyou", 26000000L)
                    )
                )
            )
        }

        service.confirmCardanoBills()

        verify(exactly = 1) { billRepository.save(match {
            it.amountReceived == 0L && it.paymentProcessedTime != null
        }) }
        verify { rabbitTemplate wasNot Called }
    }

    @Test
    fun `transaction is ignored if not matching the expected receive address`() {
        val billDto = makeBillDto()
        val now = OffsetDateTime.now()
        every { billRepository.findAllByChannelAndAmountReceivedIsNullAndTransactionIdIsNotNull("cardano") } answers {
            listOf(billFromDto(billDto, now).also { it.transactionId = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b" })
        }
        every { billRepository.save(any()) } returnsArgument 0
        every { verifyService.getTransactionSummary("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b") } answers {
            Mono.just(
                TransactionSummaryDto(
                    "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
                    now,
                    listOf(
                        TxOutputSummaryDto("addr1itsnotyou", 26000000L)
                    )
                )
            )
        }

        service.confirmCardanoBills()

        verify(exactly = 0) { billRepository.save(any()) }
        verify { rabbitTemplate wasNot Called }
    }

    @Test
    fun `transaction is saved but no confirmation message sent if amount not enough`() {
        val billDto = makeBillDto()
        val now = OffsetDateTime.now()
        every { billRepository.findAllByChannelAndAmountReceivedIsNullAndTransactionIdIsNotNull("cardano") } answers {
            listOf(billFromDto(billDto, now).also { it.transactionId = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b" })
        }
        every { billRepository.save(any()) } returnsArgument 0
        every { verifyService.getTransactionSummary("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b") } answers {
            Mono.just(
                TransactionSummaryDto(
                    "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
                    now,
                    listOf(
                        TxOutputSummaryDto("addr1itsyou", 24000000L)
                    )
                )
            )
        }

        service.confirmCardanoBills()

        verify(exactly = 1) { billRepository.save(match {
            it.amountReceived == 24000000L && it.paymentProcessedTime != null
        }) }
        verify { rabbitTemplate wasNot Called }
    }

    private fun makeBillDto(amountReceived: Long? = null, paymentProcessedTime: OffsetDateTime? = null) = BillDto(
        accountId = 12,
        createTime = OffsetDateTime.parse("2021-01-01T00:00:00Z"),
        id = 209,
        channel = "cardano",
        currency = Currency.LOVELACE_ADA,
        order = OrderDto(
            id = 85,
            items = listOf(
                OrderItemDto("stuff", 17)
            )
        ),
        amountRequested = 25000000L,
        amountReceived = amountReceived,
        paymentProcessedTime = paymentProcessedTime,
    )
}
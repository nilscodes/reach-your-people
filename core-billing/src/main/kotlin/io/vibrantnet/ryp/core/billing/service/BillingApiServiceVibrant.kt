package io.vibrantnet.ryp.core.billing.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ryp.core.billing.model.BillDto
import io.vibrantnet.ryp.core.billing.CoreBillingConfiguration
import io.vibrantnet.ryp.core.billing.persistence.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

private val logger = KotlinLogging.logger {}

@Service
class BillingApiServiceVibrant(
    private val config: CoreBillingConfiguration,
    private val verifyService: VerifyService,
    private val billRepository: BillRepository,
    private val orderRepository: OrderRepository,
    private val rabbitTemplate: RabbitTemplate,
) : BillingApiService {
    @Transactional
    override fun createBill(accountId: Long, bill: BillDto): Mono<BillDto> {
        // TODO verify minimum requested price is met
        // TODO verify account exists?

        val newOrder = orderRepository.save(Order(
            items = bill.order.items.map { OrderItem(it.type, it.amount) }.toMutableList(),
        ))

        val newBill = Bill(
            accountId = accountId,
            channel = bill.channel,
            currency = bill.currency,
            amountRequested = bill.amountRequested,
            transactionId = bill.transactionId,
            order = newOrder,
            createTime = OffsetDateTime.now(),
        )
        return Mono.just(billRepository.save(newBill).toDto())
    }

    override fun getBillsForAccount(accountId: Long): Flux<BillDto> {
        return Flux.fromIterable(billRepository.findAllByAccountId(accountId).map { it.toDto() })
    }

    @Scheduled(fixedRate = 10000)
    fun confirmCardanoBills() {
        logger.debug { "Checking for unpaid cardano bills" }
        val unpaidCardanoBills =
            billRepository.findAllByChannelAndAmountReceivedIsNullAndTransactionIdIsNotNull("cardano")
        logger.info { "Cardano bill check: Found ${unpaidCardanoBills.size} unpaid cardano bills" }
        unpaidCardanoBills.forEach { bill ->
            logger.debug { "Checking transaction summary for bill ${bill.id} with transaction ID ${bill.transactionId}" }
            verifyService.getTransactionSummary(bill.transactionId!!)
                .doOnNext { transactionSummary ->
                    if (transactionSummary.transactionTime.plusSeconds(config.payment.transactionOrderTimeToleranceSeconds).isBefore(bill.createTime)) {
                        logger.warn { "Transaction for bill ${bill.id} with transaction ID ${bill.transactionId} with a timestamp of ${transactionSummary.transactionTime} is older than the order itself: ${bill.createTime} . Marking as invalid." }
                        bill.amountReceived = 0
                        bill.paymentProcessedTime = OffsetDateTime.now()
                        billRepository.save(bill)
                        return@doOnNext
                    }
                    logger.debug { "Transaction summary for bill ${bill.id} with transaction ID ${bill.transactionId}: $transactionSummary" }
                    transactionSummary.outputs.forEach { output ->
                        if (output.address == config.payment.receiveAddress) {
                            bill.amountReceived = output.lovelace
                            bill.paymentProcessedTime = OffsetDateTime.now()
                            billRepository.save(bill)
                            if (bill.amountReceived!! >= bill.amountRequested) {
                                logger.info { "Payment for Cardano bill ${bill.id} with transaction ID ${bill.transactionId} is complete" }
                                rabbitTemplate.convertAndSend("orderconfirmations", bill.toDto())
                            } else {
                                logger.warn { "Payment for Cardano bill ${bill.id} with transaction ID ${bill.transactionId} is incomplete" }
                            }
                        }
                    }
                }
                .onErrorResume { error ->
                    logger.error(error) { "Error checking transaction summary for bill ${bill.id} with transaction ID ${bill.transactionId}" }
                    Mono.empty()
                }
                .subscribe()
        }
    }
}
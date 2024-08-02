package io.vibrantnet.ryp.core.billing.service

import io.ryp.cardano.model.TransactionSummaryDto
import reactor.core.publisher.Mono

fun interface VerifyService {
    fun getTransactionSummary(
        transactionHash: String,
    ): Mono<TransactionSummaryDto>
}
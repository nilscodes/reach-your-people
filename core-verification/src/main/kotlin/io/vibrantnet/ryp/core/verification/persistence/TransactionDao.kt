package io.vibrantnet.ryp.core.verification.persistence

import io.ryp.cardano.model.TransactionSummaryDto
import reactor.core.publisher.Mono

fun interface TransactionDao {
    fun getTransactionSummary(transactionHash: String): Mono<TransactionSummaryDto>
}
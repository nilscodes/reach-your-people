package io.vibrantnet.ryp.core.verification.service

import io.ryp.cardano.model.TransactionSummaryDto
import reactor.core.publisher.Mono

fun interface TransactionsApiService {

    /**
     * GET /transactions/{transactionHash}/summary : Get transaction summary
     *
     * @param transactionHash The hash of the transaction (required)
     * @return The payment summary of this transaction, which includes output addresses and summarized amounts of transferred ADA/Lovelace (status code 200)
     * @see TransactionsApi#getTransactionSummary
     */
    fun getTransactionSummary(transactionHash: String): Mono<TransactionSummaryDto>
}

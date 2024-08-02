package io.vibrantnet.ryp.core.verification.persistence

import io.ryp.cardano.model.TransactionSummaryDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "blockfrost")
class TransactionDaoBlockfrost : TransactionDao {
    override fun getTransactionSummary(transactionHash: String): Mono<TransactionSummaryDto> {
        TODO("Not yet implemented")
    }
}
package io.vibrantnet.ryp.core.billing.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BillRepository: CrudRepository<Bill, Int> {
    fun findAllByAccountId(accountId: Long): List<Bill>
    fun findAllByChannelAndAmountReceivedIsNullAndTransactionIdIsNotNull(channel: String): List<Bill>
}
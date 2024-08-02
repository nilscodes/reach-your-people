package io.vibrantnet.ryp.core.verification.service

import io.vibrantnet.ryp.core.verification.persistence.TransactionDao
import org.springframework.stereotype.Service

@Service
class TransactionsApiServiceVibrant(
    private val transactionDao: TransactionDao,
) : TransactionsApiService {
    override fun getTransactionSummary(transactionHash: String) = transactionDao.getTransactionSummary(transactionHash)
}
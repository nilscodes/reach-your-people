package io.vibrantnet.ryp.core.verification.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.ryp.cardano.model.TransactionSummaryDto
import io.vibrantnet.ryp.core.verification.persistence.TransactionDao
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime

internal class TransactionsApiServiceVibrantTest {
    private val transactionDao = mockk<TransactionDao>()
    private val service = TransactionsApiServiceVibrant(transactionDao)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `getting a transaction summary works`() {
        val transactionHash = "9f83e5484f543e05b52e99988272a31da373f3aab4c064c76db96643a355d9dc"
        val expected = TransactionSummaryDto(transactionHash, OffsetDateTime.now(), listOf())
        every { transactionDao.getTransactionSummary(transactionHash) } answers { Mono.just(expected) }

        val result = service.getTransactionSummary(transactionHash)

        StepVerifier.create(result)
            .expectNext(expected)
            .verifyComplete()
    }

}
package io.vibrantnet.ryp.core.verification.persistence

import io.mockk.every
import io.mockk.mockk
import io.ryp.cardano.model.TransactionSummaryDto
import io.ryp.cardano.model.TxOutputSummaryDto
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import reactor.test.StepVerifier
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class TransactionDaoCardanoDbSyncTest {
    @Test
    fun `retrieval of transaction that exists provides all outputs`() {
        val jdbcTemplate = mockk<JdbcTemplate>()
        val resultMock1 = mockk<ResultSet>()
        val resultMock2 = mockk<ResultSet>()
        val transactionDao = TransactionDaoCardanoDbSync(jdbcTemplate)

        val proposal = TransactionSummaryDto("hash", OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC), listOf(
            TxOutputSummaryDto("address1", 1),
            TxOutputSummaryDto("address2", 2),
        ))

        every { resultMock1.getLong("total_lovelace") } returns proposal.outputs[0].lovelace
        every { resultMock1.getString("address") } returns proposal.outputs[0].address
        every { resultMock1.getTimestamp("block_time", any()) } returns java.sql.Timestamp.from(proposal.transactionTime.toInstant())
        every { resultMock2.getLong("total_lovelace") } returns proposal.outputs[1].lovelace
        every { resultMock2.getString("address") } returns proposal.outputs[1].address
        every { resultMock2.getTimestamp("block_time", any()) } returns java.sql.Timestamp.from(proposal.transactionTime.toInstant())

        every {
            jdbcTemplate.query(any(), any<RowMapper<TxOutputSummaryDto>>(), any<Long>())
        } answers {
            val rowMapper = arg<RowMapper<TxOutputSummaryDto>>(1)
            listOf(resultMock1, resultMock2).map { rowMapper.mapRow(it, 0) }
        }

        val result = transactionDao.getTransactionSummary("hash")
        StepVerifier.create(result)
            .expectNext(proposal)
            .verifyComplete()
    }

    @Test
    fun `retrieval of transaction with no outputs gives error`() {
        val jdbcTemplate = mockk<JdbcTemplate>()
        val transactionDao = TransactionDaoCardanoDbSync(jdbcTemplate)

        every {
            jdbcTemplate.query(any(), any<RowMapper<TxOutputSummaryDto>>(), any<Long>())
        } answers {
            emptyList<TxOutputSummaryDto>()
        }

        val result = transactionDao.getTransactionSummary("hash")
        StepVerifier.create(result)
            .expectError(NoSuchElementException::class.java)
            .verify()
    }
}
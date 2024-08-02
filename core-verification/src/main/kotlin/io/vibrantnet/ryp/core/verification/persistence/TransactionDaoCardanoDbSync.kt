package io.vibrantnet.ryp.core.verification.persistence

import io.ryp.cardano.model.TransactionSummaryDto
import io.ryp.cardano.model.TxOutputSummaryDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.ZoneOffset
import java.util.*

const val SQL_GET_TRANSACTION_SUMMARY = """
            SELECT
                tx_out.address,
                SUM(tx_out.value) AS total_lovelace,
                MAX(b.time) AS block_time
            FROM
                tx
                    INNER JOIN
                tx_out ON tx.id = tx_out.tx_id
                    INNER JOIN
                block b ON tx.block_id = b.id
            WHERE
                tx.hash = decode(?, 'hex')
            GROUP BY
                tx_out.address
"""

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "cardano-db-sync")
class TransactionDaoCardanoDbSync(
    private val jdbcTemplate: JdbcTemplate
) : TransactionDao {
    override fun getTransactionSummary(transactionHash: String): Mono<TransactionSummaryDto> {
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        var txTimestamp = Date()
        val outputs = jdbcTemplate.query(
            SQL_GET_TRANSACTION_SUMMARY,
            { rs, _ ->
                txTimestamp = rs.getTimestamp("block_time", utcCalendar)
                TxOutputSummaryDto(rs.getString("address"), rs.getLong("total_lovelace"))
            },
            transactionHash
        )
        return if (outputs.isNotEmpty()) {
            val blockTime = txTimestamp.toInstant().atOffset(ZoneOffset.UTC)
            Mono.just(TransactionSummaryDto(transactionHash, blockTime, outputs))
        } else {
            Mono.error(NoSuchElementException("No transaction found with hash $transactionHash"))
        }
    }
}
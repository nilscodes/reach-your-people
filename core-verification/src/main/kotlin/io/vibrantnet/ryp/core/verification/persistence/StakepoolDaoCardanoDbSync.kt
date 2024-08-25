package io.vibrantnet.ryp.core.verification.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import io.ryp.cardano.model.stakepools.StakepoolDetailsDto
import io.ryp.cardano.model.stakepools.StakepoolDelegationInfoDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.sql.ResultSet

const val SQL_GET_STAKEPOOL_DETAILS = """
            SELECT o.ticker_name, h.hash_raw, o.json
            FROM pool_hash h
            JOIN off_chain_pool_data o
            ON h.id = o.pool_id
            WHERE hash_raw = decode(?, 'hex')
            ORDER BY o.id DESC
            LIMIT 1
"""

const val SQL_GET_STAKEPOOL_DETAILS_FOR_STAKE_ADDRESS = """
    SELECT o.ticker_name, h.hash_raw, o.json
        FROM delegation d1, pool_hash h, stake_address sa, off_chain_pool_data o
    WHERE h.id=d1.pool_hash_id
        AND h.id=o.pool_id
        AND sa.view=?
        AND d1.addr_id=sa.id
        AND NOT EXISTS
            (SELECT TRUE
                FROM delegation d2
            WHERE d2.addr_id=d1.addr_id
                AND d2.tx_id>d1.tx_id
            )
        AND NOT EXISTS
            (SELECT TRUE
                FROM stake_deregistration
            WHERE stake_deregistration.addr_id=d1.addr_id
                AND stake_deregistration.tx_id>d1.tx_id
            )
    ORDER BY o.id DESC
    LIMIT 1
"""

const val SQL_GET_DELEGATION_TO_POOL_IN_EPOCH = """
    SELECT e.amount, sa.view
        FROM epoch_stake e
        JOIN pool_hash h
            ON e.pool_id=h.id
        JOIN stake_address sa
            ON e.addr_id = sa.id
    WHERE h.hash_raw=decode(?, 'hex')
        AND epoch_no=?
"""

const val SQL_GET_ACTIVE_DELEGATION_TO_POOL_WITHOUT_AMOUNT = """
        SELECT sa.view
          FROM delegation d1, pool_hash, stake_address sa
          WHERE pool_hash.id=d1.pool_hash_id
            AND pool_hash.hash_raw=decode(?, 'hex')
            AND d1.addr_id=sa.id
            AND NOT EXISTS
              (SELECT TRUE
               FROM delegation d2
               WHERE d2.addr_id=d1.addr_id
                 AND d2.tx_id>d1.tx_id)
            AND NOT EXISTS
              (SELECT TRUE
               FROM stake_deregistration
               WHERE stake_deregistration.addr_id=d1.addr_id
                 AND stake_deregistration.tx_id>d1.tx_id)
"""

const val SQL_GET_ACTIVE_DELEGATION_TO_POOL = """
        WITH stake AS
                 (SELECT d1.addr_id, sa.view
                  FROM delegation d1, pool_hash, stake_address sa
                  WHERE pool_hash.id=d1.pool_hash_id
                    AND pool_hash.hash_raw=decode(?, 'hex')
                    AND d1.addr_id=sa.id
                    AND NOT EXISTS
                      (SELECT TRUE
                       FROM delegation d2
                       WHERE d2.addr_id=d1.addr_id
                         AND d2.tx_id>d1.tx_id)
                    AND NOT EXISTS
                      (SELECT TRUE
                       FROM stake_deregistration
                       WHERE stake_deregistration.addr_id=d1.addr_id
                         AND stake_deregistration.tx_id>d1.tx_id))
        SELECT sum(total) AS amount, view
        FROM
            (SELECT sum(value) total, stake.view AS view
             FROM utxo_view
                      INNER JOIN stake ON utxo_view.stake_address_id=stake.addr_id
             GROUP BY stake.view
             UNION SELECT sum(amount), stake.view
             FROM reward
                      INNER JOIN stake ON reward.addr_id=stake.addr_id
             WHERE reward.spendable_epoch <= (SELECT MAX(epoch_no) FROM block)
             GROUP BY stake.view
             UNION SELECT -sum(amount), stake.view
             FROM withdrawal
                      INNER JOIN stake ON withdrawal.addr_id=stake.addr_id
             GROUP BY stake.view
            ) AS t
        GROUP BY view
"""

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "cardano-db-sync")
class StakepoolDaoCardanoDbSync(
    private val jdbcTemplate: JdbcTemplate,
    private val objectMapper: ObjectMapper,
) : StakepoolDao {
    override fun getStakepoolDetails(poolHash: String): Mono<StakepoolDetailsDto> {
        return try {
            Mono.just(jdbcTemplate.queryForObject(SQL_GET_STAKEPOOL_DETAILS.trimIndent(), { rs, _ ->
                mapStakepoolDetails(rs)
            }, poolHash)!!)
        } catch (e: EmptyResultDataAccessException) {
            Mono.error(NoSuchElementException("No stakepool details found for the given pool hash $poolHash"))
        }
    }

    override fun getStakepoolDetailsForStakeAddress(stakeAddress: String): Mono<StakepoolDetailsDto> {
        return try {
            Mono.just(jdbcTemplate.queryForObject(SQL_GET_STAKEPOOL_DETAILS_FOR_STAKE_ADDRESS.trimIndent(), { rs, _ ->
                mapStakepoolDetails(rs)
            }, stakeAddress)!!)
        } catch (e: EmptyResultDataAccessException) {
            Mono.error(NoSuchElementException("No stakepool details found for the given stake address $stakeAddress"))
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun mapStakepoolDetails(rs: ResultSet): StakepoolDetailsDto {
        val poolInfo = objectMapper.readTree(rs.getString("json"))
        return StakepoolDetailsDto(
            poolHash = rs.getBytes("hash_raw").toHexString(),
            ticker = rs.getString("ticker_name"),
            name = poolInfo["name"].asText(),
            description = poolInfo["description"].asText(),
            homepage = poolInfo["homepage"].asText(),
        )
    }

    override fun getActiveDelegation(poolHash: String): List<StakepoolDelegationInfoDto> {
        return jdbcTemplate.query(SQL_GET_ACTIVE_DELEGATION_TO_POOL, { rs, _ ->
            StakepoolDelegationInfoDto(poolHash, rs.getLong("amount"), rs.getString("view"))
        }, poolHash)
    }

    override fun getActiveDelegationWithoutAmount(poolHash: String): List<StakepoolDelegationInfoDto> {
        return jdbcTemplate.query(SQL_GET_ACTIVE_DELEGATION_TO_POOL_WITHOUT_AMOUNT, { rs, _ ->
            StakepoolDelegationInfoDto(poolHash, 1, rs.getString("view"))
        }, poolHash)
    }

    override fun getDelegationInEpoch(poolHash: String, epochNo: Int): List<StakepoolDelegationInfoDto> {
        return jdbcTemplate.query(SQL_GET_DELEGATION_TO_POOL_IN_EPOCH, { rs, _ ->
            StakepoolDelegationInfoDto(poolHash, rs.getLong("amount"), rs.getString("view"))
        }, poolHash, epochNo)
    }
}
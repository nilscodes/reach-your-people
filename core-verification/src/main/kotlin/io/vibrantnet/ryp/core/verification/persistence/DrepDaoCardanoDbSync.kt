package io.vibrantnet.ryp.core.verification.persistence

import io.ryp.cardano.model.DRepDelegationInfoDto
import io.ryp.cardano.model.DRepDetailsDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.sql.ResultSet

const val SQL_GET_DREP_DETAILS = """
     WITH LatestDrepDistr AS (
         SELECT
             hash_id,
             MAX(epoch_no) AS latest_epoch
         FROM
             drep_distr
         GROUP BY
             hash_id
     ),
     DrepDistrDetails AS (
         SELECT
             dd.hash_id,
             dd.amount,
             dd.epoch_no,
             dd.active_until
         FROM
             drep_distr dd
                 JOIN
             LatestDrepDistr ld
             ON
                 dd.hash_id = ld.hash_id AND dd.epoch_no = ld.latest_epoch
     )
    SELECT
        h.raw,
        h.view,
        dd.epoch_no,
        dd.active_until,
        dd.amount
    FROM
        drep_hash h
            JOIN
        DrepDistrDetails dd
        ON h.id = dd.hash_id
    WHERE
        h.raw = decode(?, 'hex')
"""

const val SQL_GET_DREP_DETAILS_FOR_STAKE_ADDRESS = """
    WITH LatestDelegationVote AS (
        SELECT
            addr_id,
            MAX(tx_id) AS max_tx_id
        FROM
            delegation_vote
        GROUP BY
            addr_id
    ),
     LatestDrepDistr AS (
         SELECT
             hash_id,
             MAX(epoch_no) AS latest_epoch
         FROM
             drep_distr
         GROUP BY
             hash_id
     ),
     DrepDistrDetails AS (
         SELECT
             dd.hash_id,
             dd.amount,
             dd.epoch_no,
             dd.active_until
         FROM
             drep_distr dd
                 JOIN
             LatestDrepDistr ld
             ON
                 dd.hash_id = ld.hash_id AND dd.epoch_no = ld.latest_epoch
     )
    SELECT
        h.raw,
        h.view,
        dd.epoch_no,
        dd.active_until,
        dd.amount
    FROM
        stake_address sa
            JOIN
        delegation_vote dv
        ON sa.id = dv.addr_id
            JOIN
        drep_hash h
        ON dv.drep_hash_id = h.id
            JOIN
        LatestDelegationVote ldv
        ON dv.addr_id = ldv.addr_id
            AND dv.tx_id = ldv.max_tx_id
            LEFT JOIN
        DrepDistrDetails dd
        ON h.id = dd.hash_id
    WHERE
        sa.view = ?
"""

const val SQL_GET_ACTIVE_DELEGATION_TO_DREP_WITHOUT_AMOUNT = """
    WITH LatestDelegationVote AS (
    SELECT
        addr_id,
        MAX(tx_id) AS max_tx_id
    FROM
        delegation_vote
    GROUP BY
        addr_id
    )
    SELECT
        sa.view
    FROM
        stake_address sa
    JOIN
        delegation_vote dv
        ON sa.id = dv.addr_id
    JOIN
        drep_hash h
        ON dv.drep_hash_id = h.id
    JOIN
        LatestDelegationVote ldv
        ON dv.addr_id = ldv.addr_id
        AND dv.tx_id = ldv.max_tx_id
    WHERE
        h.raw = decode(?, 'hex')
"""

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "cardano-db-sync")
class DrepDaoCardanoDbSync(
    private val jdbcTemplate: JdbcTemplate,
) : DrepDao {
    override fun getDrepDetails(drepId: String): Mono<DRepDetailsDto> {
        return try {
            Mono.just(jdbcTemplate.queryForObject(SQL_GET_DREP_DETAILS.trimIndent(), { rs, _ ->
                mapDrepDetails(rs)
            }, drepId)!!)
        } catch (e: EmptyResultDataAccessException) {
            Mono.error(NoSuchElementException("No DRep details found for the given dRep ID $drepId"))
        }
    }

    override fun getDrepDetailsForStakeAddress(stakeAddress: String): Mono<DRepDetailsDto> {
        return try {
            Mono.just(jdbcTemplate.queryForObject(SQL_GET_DREP_DETAILS_FOR_STAKE_ADDRESS.trimIndent(), { rs, _ ->
                mapDrepDetails(rs)
            }, stakeAddress)!!)
        } catch (e: EmptyResultDataAccessException) {
            Mono.error(NoSuchElementException("No DRep details found for the given stake address $stakeAddress"))
        }
    }

    override fun getActiveDelegationWithoutAmount(drepId: String): List<DRepDelegationInfoDto> {
        return jdbcTemplate.query(SQL_GET_ACTIVE_DELEGATION_TO_DREP_WITHOUT_AMOUNT, { rs, _ ->
            DRepDelegationInfoDto(drepId, 1, rs.getString("view"))
        }, drepId)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun mapDrepDetails(rs: ResultSet): DRepDetailsDto {
        var currentEpoch: Int = rs.getInt("epoch_no")
        currentEpoch = if (rs.wasNull()) 0 else currentEpoch
        var activeUntil: Int? = rs.getInt("active_until")
        activeUntil = if (rs.wasNull()) null else activeUntil
        var delegation: Long = rs.getLong("amount")
        delegation = if (rs.wasNull()) 0 else delegation
        return DRepDetailsDto(
            drepId = rs.getBytes("raw").toHexString(),
            drepView = rs.getString("view"),
            displayName = rs.getString("view"),
            currentEpoch = currentEpoch,
            activeUntil = activeUntil,
            delegation = delegation,
        )
    }


}
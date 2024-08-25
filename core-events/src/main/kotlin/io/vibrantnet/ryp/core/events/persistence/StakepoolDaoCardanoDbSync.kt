package io.vibrantnet.ryp.core.events.persistence

import io.vibrantnet.ryp.core.events.model.StakepoolRetirementDto
import io.vibrantnet.ryp.core.events.model.StakepoolVoteDetailsDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.sql.ResultSet

const val SQL_GET_RETIRED_STAKEPOOLS_ABOVE_ID = """
SELECT 
    pr.id AS id,
    encode(tx.hash, 'hex') as transactionHash,
    pr.cert_index as index,
    encode(ph.hash_raw, 'hex') AS poolHash
FROM 
    pool_retire pr
JOIN
    tx ON pr.announced_tx_id=tx.id
JOIN 
    pool_hash ph ON pr.hash_id = ph.id
WHERE 
    pr.id > ?

"""

const val SQL_GET_SPO_VOTES_ABOVE_ID = """
SELECT 
    vp.id AS id,
    encode(tx.hash, 'hex') as transactionHash,
    vp.index as index,
    vp.gov_action_proposal_id AS proposalId,
    encode(ph.hash_raw, 'hex') AS poolHash,
    va.url AS votingAnchorUrl
FROM 
    voting_procedure vp
JOIN
    tx ON vp.tx_id=tx.id
JOIN 
    pool_hash ph ON vp.pool_voter = ph.id
LEFT JOIN 
    voting_anchor va ON vp.voting_anchor_id = va.id
WHERE 
    vp.id > ?
    AND vp.pool_voter IS NOT NULL

"""

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "cardano-db-sync")
class StakepoolDaoCardanoDbSync(
    private val jdbcTemplate: JdbcTemplate,
) : StakepoolDao {
    override fun getStakepoolRetirementsWithIdsHigherThan(voteId: Long): Flux<StakepoolRetirementDto> {
        val newVotes = jdbcTemplate.query(SQL_GET_RETIRED_STAKEPOOLS_ABOVE_ID, { rs, _ ->
            mapRetiredStakepool(rs)
        }, voteId)
        return Flux.fromIterable(newVotes)
    }

    private fun mapRetiredStakepool(rs: ResultSet) = StakepoolRetirementDto(
        id = rs.getLong("id"),
        transactionHash = rs.getString("transactionHash"),
        transactionIndex = rs.getInt("index"),
        poolHash = rs.getString("poolHash"),
    )

    override fun getStakepoolVotesWithIdsHigherThan(voteId: Long): Flux<StakepoolVoteDetailsDto> {
        val newVotes = jdbcTemplate.query(SQL_GET_SPO_VOTES_ABOVE_ID, { rs, _ ->
            mapVotingProcedure(rs)
        }, voteId)
        return Flux.fromIterable(newVotes)
    }

    private fun mapVotingProcedure(rs: ResultSet) = StakepoolVoteDetailsDto(
        id = rs.getLong("id"),
        transactionHash = rs.getString("transactionHash"),
        transactionIndex = rs.getInt("index"),
        proposalId = rs.getLong("proposalId"),
        poolHash = rs.getString("poolHash"),
        votingAnchorUrl = rs.getString("votingAnchorUrl")
    )

}
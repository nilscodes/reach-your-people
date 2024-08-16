package io.vibrantnet.ryp.core.events.persistence

import io.vibrantnet.ryp.core.events.model.DRepVoteDetailsDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.sql.ResultSet

const val SQL_GET_DREP_VOTES_ABOVE_ID = """
    SELECT 
    vp.id AS id,
    encode(tx.hash, 'hex') as transactionHash,
    vp.gov_action_proposal_id AS proposalId,
    encode(dh.raw, 'hex') AS drepId,
    va.url AS votingAnchorUrl
FROM 
    voting_procedure vp
JOIN
    tx ON vp.tx_id=tx.id
JOIN 
    drep_hash dh ON vp.drep_voter = dh.id
LEFT JOIN 
    voting_anchor va ON vp.voting_anchor_id = va.id
WHERE 
    vp.id > ?
    AND vp.drep_voter IS NOT NULL

"""

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "cardano-db-sync")
class DrepVoteDaoCardanoDbSync(
    private val jdbcTemplate: JdbcTemplate,
) : DrepVoteDao {
    override fun getDrepVotesWithIdsHigherThan(voteId: Long): Flux<DRepVoteDetailsDto> {
            val newVotes = jdbcTemplate.query(SQL_GET_DREP_VOTES_ABOVE_ID, { rs, _ ->
                mapVotingProcedure(rs)
            }, voteId)
        return Flux.fromIterable(newVotes)
    }


    private fun mapVotingProcedure(rs: ResultSet) = DRepVoteDetailsDto(
            id = rs.getLong("id"),
            transactionHash = rs.getString("transactionHash"),
            proposalId = rs.getLong("proposalId"),
            drepId = rs.getString("drepId"),
            votingAnchorUrl = rs.getString("votingAnchorUrl")
        )

}
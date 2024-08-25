package io.vibrantnet.ryp.core.events.persistence

import io.ryp.cardano.model.governance.GovernanceActionType
import io.vibrantnet.ryp.core.events.model.GovernanceActionProposalDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.sql.ResultSet

const val SQL_GET_GOVERNANCE_ACTION_PROPOSALS_ABOVE_ID = """
SELECT 
    gap.id AS proposalId,
    encode(tx.hash, 'hex') as transactionHash,
    gap.index as index,
    gap.type as type,
    va.url AS votingAnchorUrl
FROM 
    gov_action_proposal gap
JOIN
    tx ON gap.tx_id=tx.id
LEFT JOIN 
    voting_anchor va ON gap.voting_anchor_id = va.id
WHERE 
    gap.id > ?
"""

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "cardano-db-sync")
class GovernanceActionDaoCardanoDbSync(
    private val jdbcTemplate: JdbcTemplate,
) : GovernanceActionDao {
    override fun getGovernanceActionsWithIdsHigherThan(voteId: Long): Flux<GovernanceActionProposalDto> {
        val newGovernanceActions = jdbcTemplate.query(SQL_GET_GOVERNANCE_ACTION_PROPOSALS_ABOVE_ID, { rs, _ ->
            mapGovernanceActionProposal(rs)
        }, voteId)
        return Flux.fromIterable(newGovernanceActions)
    }

    private fun mapGovernanceActionProposal(rs: ResultSet) = GovernanceActionProposalDto(
        proposalId = rs.getLong("proposalId"),
        transactionHash = rs.getString("transactionHash"),
        transactionIndex = rs.getInt("index"),
        type = GovernanceActionType.fromString(rs.getString("type")),
        votingAnchorUrl = rs.getString("votingAnchorUrl")
    )
}
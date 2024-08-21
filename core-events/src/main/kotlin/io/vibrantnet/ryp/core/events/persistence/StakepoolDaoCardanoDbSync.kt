package io.vibrantnet.ryp.core.events.persistence

import io.vibrantnet.ryp.core.events.model.StakepoolRetirementDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.sql.ResultSet

const val SQL_GET_RETIRED_STAKEPOOLS_ABOVE_ID = """
    SELECT 
    pr.id AS id,
    encode(tx.hash, 'hex') as transactionHash,
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
            poolHash = rs.getString("poolHash"),
        )

}
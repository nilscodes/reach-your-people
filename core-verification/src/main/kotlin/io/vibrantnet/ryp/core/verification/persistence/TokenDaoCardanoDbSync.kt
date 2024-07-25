package io.vibrantnet.ryp.core.verification.persistence

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Types
import java.util.*

const val GET_SNAPSHOT_OF_STAKES_BY_POLICIES =
    "SELECT encode(ma.policy, 'hex') AS policy, sa.view AS stakeview, SUM(mto.quantity) AS number FROM utxo_view u JOIN ma_tx_out mto ON u.id = mto.tx_out_id JOIN multi_asset ma ON mto.ident = ma.id JOIN stake_address sa ON u.stake_address_id = sa.id WHERE ma.policy IN (%s) GROUP BY policy, sa.view"
const val GET_ALL_MULTI_ASSET_NAMES_IN_STAKE_ADDRESS_ALL =
    "SELECT encode(ma.policy, 'hex') AS policy, SUM(mto.quantity) as number FROM utxo_view u JOIN ma_tx_out mto ON u.id = mto.tx_out_id JOIN multi_asset ma ON mto.ident = ma.id JOIN stake_address sa ON u.stake_address_id = sa.id WHERE sa.view=? GROUP BY policy"

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "cardano-db-sync")
class TokenDaoCardanoDbSync(
    private val jdbcTemplate: JdbcTemplate
) : TokenDao {

    override fun getMultiAssetCountSnapshotForPolicyId(policyIds: List<PolicyId>): List<TokenOwnershipInfoWithAssetCount> {
        val policyIdInClause = Collections.nCopies(policyIds.size, "decode(?, 'hex')").joinToString(",")
        val sql = String.format(GET_SNAPSHOT_OF_STAKES_BY_POLICIES, policyIdInClause)
        val sqlParameters = policyIds.map { it.policyId }
        val sqlParameterTypes = Collections.nCopies(policyIds.size, Types.VARCHAR).toIntArray()
        return jdbcTemplate.query(sql, sqlParameters.toTypedArray(), sqlParameterTypes) { rs, _ ->
            TokenOwnershipInfoWithAssetCount(rs.getString("stakeview"), rs.getString("policy"), rs.getLong("number"))
        }
    }

    override fun getMultiAssetListForStakeAddress(stakeAddress: String): List<TokenOwnershipInfoWithAssetCount> {
        return jdbcTemplate.query(GET_ALL_MULTI_ASSET_NAMES_IN_STAKE_ADDRESS_ALL, { rs, _ ->
            TokenOwnershipInfoWithAssetCount(stakeAddress = stakeAddress, policyIdWithOptionalAssetFingerprint = rs.getString("policy"), assetCount = rs.getLong("number"))
        }, stakeAddress)
    }

}
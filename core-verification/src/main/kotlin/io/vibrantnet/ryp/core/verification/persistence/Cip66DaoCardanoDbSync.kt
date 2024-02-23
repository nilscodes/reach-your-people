package io.vibrantnet.ryp.core.verification.persistence

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.hazelnet.cardano.connect.data.token.PolicyId
import io.vibrantnet.ryp.core.verification.model.Cip66PayloadDto
import io.vibrantnet.ryp.core.verification.model.Cip66PolicyDto
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

const val SQL_GET_CIP_66_METADATA_FROM_POLICY = """
            SELECT
                ma.fingerprint,
                encode(ma.name, 'hex') as name,
                encode(ma.policy, 'hex') as policy,
                mtm.quantity,
                encode(tx.hash, 'hex') as hash,
                tm.json
            FROM
                multi_asset ma
                JOIN ma_tx_mint mtm
                    ON ma.id = mtm.ident
                JOIN tx_metadata tm
                    ON mtm.tx_id = tm.tx_id
                JOIN tx
                    ON mtm.tx_id = tx.id
            WHERE
                policy=DECODE(?, 'hex')
                AND name=cast(? as asset32type)
                AND key=?
                AND mtm.quantity>0
            ORDER BY mtm.tx_id DESC
            LIMIT 1
        """

@Repository
class Cip66DaoCardanoDbSync(
    private val jdbcTemplate: JdbcTemplate
) : Cip66Dao {

    override fun getCip66Payload(policyId: String): Cip66PayloadDto {
        return jdbcTemplate.queryForObject(SQL_GET_CIP_66_METADATA_FROM_POLICY.trimIndent(), { rs, _ ->
            mapCip66Info(rs)
        }, policyId, "", CIP66_METADATA_KEY)!!
    }

    private fun mapCip66Info(
        rs: ResultSet,
    ): Cip66PayloadDto {
        val objectMapper = ObjectMapper()
        val mintMetadata = objectMapper.readValue(rs.getString("json"), object : TypeReference<Map<String, Any>>() {})
        val policyData = mutableMapOf<PolicyId, Cip66PolicyDto>()
        var version = DEFAULT_CIP66_VERSION
        mintMetadata.entries.forEach {
            if (it.key == "version" && it.value is String) {
                version = it.value as String
            } else {
                try {
                    val policyId = PolicyId(it.key)
                    val policyInfo = objectMapper.convertValue(it.value, Cip66PolicyDto::class.java)
                    policyData[policyId] = policyInfo
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return Cip66PayloadDto(
            version,
            policyData
        )
    }
}
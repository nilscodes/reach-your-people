package io.vibrantnet.ryp.core.verification.persistence

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vibrantnet.ryp.core.verification.model.Cip66PayloadDto
import io.vibrantnet.ryp.core.verification.model.makeCip66PayloadDtoFromMetadata
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
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
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "cardano-db-sync")
class Cip66DaoCardanoDbSync(
    private val jdbcTemplate: JdbcTemplate
) : Cip66Dao {

    override fun getCip66Payload(policyId: String): Mono<Cip66PayloadDto> {
        return Mono.just(jdbcTemplate.queryForObject(SQL_GET_CIP_66_METADATA_FROM_POLICY.trimIndent(), { rs, _ ->
            mapCip66Info(rs)
        }, policyId, "", CIP66_METADATA_KEY)!!)
    }

    private fun mapCip66Info(
        rs: ResultSet,
    ): Cip66PayloadDto {
        val objectMapper = jacksonObjectMapper().registerKotlinModule()
        val mintMetadata = objectMapper.readValue(rs.getString("json"), object : TypeReference<Map<String, Any>>() {})
        return makeCip66PayloadDtoFromMetadata(mintMetadata, objectMapper)
    }
}
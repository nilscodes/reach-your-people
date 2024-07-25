package io.vibrantnet.ryp.core.verification.persistence

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.sql.ResultSet

const val SQL_GET_VRF_VERIFICATION_KEY_HASH_FOR_POOL_HASH = """
            SELECT vrf_key_hash
            FROM pool_update
            WHERE hash_id = (
                SELECT id
                FROM pool_hash
                WHERE hash_raw = decode(?, 'hex')
            )
            ORDER BY active_epoch_no DESC, registered_tx_id DESC
            LIMIT 1
        """

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "cardano-db-sync")
class Cip22DaoCardanoDbSync(
    private val jdbcTemplate: JdbcTemplate,
) : Cip22Dao {

    override fun getVrfVerificationKeyHashForPool(poolHash: String): Mono<ByteArray> {
        return try {
            Mono.just(jdbcTemplate.queryForObject(SQL_GET_VRF_VERIFICATION_KEY_HASH_FOR_POOL_HASH.trimIndent(), { rs, _ ->
                mapVrf(rs)
            }, poolHash)!!)
        } catch (e: EmptyResultDataAccessException) {
            Mono.error(NoSuchElementException("No VRF verification key hash found for the given pool hash $poolHash"))
        }
    }

    private fun mapVrf(rs: ResultSet): ByteArray {
        return rs.getBytes("vrf_key_hash")
    }

}
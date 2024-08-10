package io.vibrantnet.ryp.core.verification.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.BinaryNode
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import io.ryp.cardano.model.StakepoolVerificationDto
import io.vibrantnet.ryp.core.verification.CoreVerificationConfiguration
import io.vibrantnet.ryp.core.verification.model.ExpiredCip22Verification
import io.vibrantnet.ryp.core.verification.model.InvalidCip22Verification
import io.vibrantnet.ryp.core.verification.persistence.Cip22Dao
import io.vibrantnet.ryp.core.verification.persistence.StakepoolDao
import io.vibrantnet.ryp.core.verification.persistence.StakepoolVerificationDocument
import io.vibrantnet.ryp.core.verification.persistence.StakepoolVerificationRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.security.SecureRandom

@Service
class PoolsApiServiceVibrant(
    private val libSodiumService: LibSodiumService,
    private val cip22Dao: Cip22Dao,
    private val stakepoolDao: StakepoolDao,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val stakepoolVerificationRepository: StakepoolVerificationRepository,
    private val objectMapper: ObjectMapper,
    private val config: CoreVerificationConfiguration,
): PoolsApiService {


    override fun getStakepoolDetails(poolHash: String) = stakepoolDao.getStakepoolDetails(poolHash)

    override fun startStakepoolVerification(poolHash: String): Mono<StakepoolVerificationDto> {
        return cip22Dao.getVrfVerificationKeyHashForPool(poolHash)
            .flatMap { _ ->
                val random64CharacterHexString = generateRandomHex64()
                val stakepoolVerification = StakepoolVerificationDto(
                    nonce = random64CharacterHexString,
                    domain = config.cip22.domain,
                    poolHash = poolHash,
                    vrfVerificationKey = null,
                    signature = null,
                    createTime = java.time.OffsetDateTime.now(),
                    expirationTime = java.time.OffsetDateTime.now().plusMinutes(config.cip22.expirationMinutes)
                )
                redisTemplate.opsForValue().set(
                    "$CIP_0022:$poolHash",
                    stakepoolVerification,
                    config.cip22.expirationMinutes,
                    java.util.concurrent.TimeUnit.MINUTES
                )
                Mono.just(stakepoolVerification)
            }
            .onErrorResume { e ->
                Mono.error(InvalidCip22Verification("Failed to start stakepool verification: ${e.message}", e))
            }
    }

    override fun testStakepoolVerification(
        poolHash: String,
        verificationNonce: String,
        stakepoolVerification: StakepoolVerificationDto
    ) = doStakepoolVerification(poolHash, verificationNonce, stakepoolVerification)

    override fun completeStakepoolVerification(
        poolHash: String,
        verificationNonce: String,
        stakepoolVerification: StakepoolVerificationDto
    ) = doStakepoolVerification(poolHash, verificationNonce, stakepoolVerification, true)

    fun doStakepoolVerification(
        poolHash: String,
        verificationNonce: String,
        stakepoolVerification: StakepoolVerificationDto,
        finalize: Boolean = false,
    ): Mono<StakepoolVerificationDto> {
        if (stakepoolVerification.vrfVerificationKey != null && stakepoolVerification.signature != null) {
            val currentVerificationRaw = redisTemplate.opsForValue().get("$CIP_0022:$poolHash")
            val currentVerification = if(currentVerificationRaw != null) objectMapper.convertValue(currentVerificationRaw, StakepoolVerificationDto::class.java) else null
            if (currentVerification != null && currentVerification.nonce == verificationNonce) {
                val cbor = parseCborFromHex(stakepoolVerification.vrfVerificationKey!!.cborHex)
                if (cbor is BinaryNode) {
                    if (cbor.binaryValue().size == VRF_VKEY_SIZE_BYTES) {
                        return cip22Dao.getVrfVerificationKeyHashForPool(poolHash)
                            .flatMap { vrfVKeyHash ->
                                if (verifyCip22MessageSignature(
                                        verificationNonce,
                                        stakepoolVerification.signature!!,
                                        config.cip22.domain,
                                        vrfVKeyHash,
                                        cbor.binaryValue(),
                                    )
                                ) {
                                    // If not finalizing, we intentionally leave the verification in Redis, as it used both for displaying verification success on the frontend, but run again on the backend on submission
                                    if (finalize) {
                                        stakepoolVerificationRepository.save(StakepoolVerificationDocument(
                                            verificationNonce = verificationNonce,
                                            verificationData = stakepoolVerification,
                                        )).subscribe()
                                        redisTemplate.delete("$CIP_0022:$poolHash")
                                    }
                                    Mono.just(stakepoolVerification)
                                } else {
                                    Mono.error(InvalidCip22Verification("Verification failed as the signature did not match the VRF Verification Key"))
                                }
                            }
                            .onErrorResume { e ->
                                Mono.error(InvalidCip22Verification("Failed to complete stakepool verification: ${e.message}", e))
                            }
                    }
                    return Mono.error(InvalidCip22Verification("VRF Verification Key must be $VRF_VKEY_SIZE_BYTES bytes. Your key had ${cbor.binaryValue().size} bytes - you have likely provided a signing key. We have ignored it to protect your sensitive information."))
                }
                return Mono.error(InvalidCip22Verification("VRF Verification Key must be a CBOR encoded binary"))
            }
            return Mono.error(ExpiredCip22Verification("Verification with nonce $verificationNonce expired"))
        }
        return Mono.error(InvalidCip22Verification("VRF Verification Key and Signature must be provided for verification"))
    }


    @OptIn(ExperimentalStdlibApi::class)
    fun verifyCip22MessageSignature(originalMessageHex: String, signedMessageHex: String, domain: String, vrfVKeyHash: ByteArray, vrfVkey: ByteArray): Boolean {
        logger.info { "Starting verification process" }
        val prefix = String.format("%s%s", CIP_0022, domain)
        logger.info { "Prefix: $prefix" }

        val challenge = org.bouncycastle.util.Arrays.concatenate(
            org.bouncycastle.util.encoders.Hex.encode(prefix.toByteArray()),
            originalMessageHex.toByteArray(StandardCharsets.UTF_8),
        )
        logger.info { "Challenge: ${challenge.toHexString()}" }

        val challengeHash = libSodiumService.cryptoBlake2bHash(org.bouncycastle.util.encoders.Hex.decode(challenge), null)
        logger.info { "Challenge Hash: ${challengeHash.toHexString()}" }

        logger.info { "Provided Vkey Hash: ${vrfVKeyHash.toHexString()}" }

        val vkeyHashVerify = libSodiumService.cryptoBlake2bHash(vrfVkey, null)
        logger.info { "Vkey Hash created from VRF VKey: ${vkeyHashVerify.toHexString()}" }

        if (!vrfVKeyHash.contentEquals(vkeyHashVerify)) {
            logger.info { "Vkey Hash does not match Vkey Hash Verify" }
            return false
        }

        val signedMessage = signedMessageHex.hexToByteArray()
        val signatureHash = try {
            libSodiumService.cryptoVrfProofToHash_ietfdraft03(signedMessage).also {
                logger.info { "Signature Hash: ${it.toHexString()}" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to convert signature to hash: ${e.message}" }
            return false
        }

        val verification = try {
            libSodiumService.cryptoVrfVerify_ietfdraft03(vrfVkey, signedMessage, challengeHash)
        } catch (e: Exception) {
            logger.error(e) { "Failed to verify VRF: ${e.message}" }
            return false
        }

        logger.info { "Verification result: ${signatureHash.contentEquals(verification)}" }
        return signatureHash.contentEquals(verification)
    }

    companion object {
        private const val CIP_0022 = "cip-0022"
        private const val VRF_VKEY_SIZE_BYTES = 32
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun parseCborFromHex(hex: String): JsonNode {
    val cborFactory = CBORFactory()
    val objectMapper = ObjectMapper(cborFactory)
    val cborBytes = hex.hexToByteArray()
    return objectMapper.readTree(cborBytes)
}

fun generateRandomHex64(): String {
    val random = SecureRandom()
    val bytes = ByteArray(32) // 32 bytes * 2 characters per byte = 64 characters
    random.nextBytes(bytes)
    return bytes.joinToString("") { "%02x".format(it) }
}

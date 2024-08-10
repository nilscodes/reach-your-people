package io.vibrantnet.ryp.core.verification.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.*
import io.ryp.cardano.model.StakepoolDetailsDto
import io.ryp.cardano.model.StakepoolVerificationDto
import io.ryp.cardano.model.VrfVerificationKey
import io.vibrantnet.ryp.core.verification.CoreVerificationConfiguration
import io.vibrantnet.ryp.core.verification.model.ExpiredCip22Verification
import io.vibrantnet.ryp.core.verification.model.InvalidCip22Verification
import io.vibrantnet.ryp.core.verification.persistence.Cip22Dao
import io.vibrantnet.ryp.core.verification.persistence.StakepoolDao
import io.vibrantnet.ryp.core.verification.persistence.StakepoolVerificationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime

internal class PoolsApiServiceVibrantTest {
    private val libSodiumService = mockk<LibSodiumService>()
    private val cip22Dao = mockk<Cip22Dao>()
    private val stakepoolDao = mockk<StakepoolDao>()
    private val opsForValue = mockk<ValueOperations<String, Any>>()
    private val redisTemplate = mockk<RedisTemplate<String, Any>>()
    private val stakepoolVerificationRepository = mockk<StakepoolVerificationRepository>()
    private val objectMapper = jacksonObjectMapper().registerKotlinModule().registerModules(JavaTimeModule())
    private val config = CoreVerificationConfiguration(ipfslink = "", blockfrost = null)
    private val service = PoolsApiServiceVibrant(
        libSodiumService,
        cip22Dao,
        stakepoolDao,
        redisTemplate,
        stakepoolVerificationRepository,
        objectMapper,
        config
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        every { redisTemplate.opsForValue() } returns opsForValue
    }

    @Test
    fun `getting stake pool details works`() {
        val stakepoolDetails = StakepoolDetailsDto(
            poolHash = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
            ticker = "VIBRN",
            name = "Vibrant",
            homepage = "https://vibrantnet.io",
            description = "Vibrant Stake Pool"
        )
        every { stakepoolDao.getStakepoolDetails(stakepoolDetails.poolHash) } answers { Mono.just(stakepoolDetails) }

        val result = service.getStakepoolDetails(stakepoolDetails.poolHash)

        StepVerifier.create(result)
            .expectNext(stakepoolDetails)
            .verifyComplete()
    }

    @Test
    fun `starting a stake pool verification with valid pool hash works`() {
        val randomByteArray = ByteArray(64)
        every { cip22Dao.getVrfVerificationKeyHashForPool(any()) } answers { Mono.just(randomByteArray) }
        every { opsForValue.set("cip-0022:0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b", any(), any(), any()) } just Runs

        val result = service.startStakepoolVerification("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b")

        StepVerifier.create(result)
            .expectNextMatches { it.nonce.length == 64
                    && it.domain == config.cip22.domain
                    && it.poolHash == "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b"
                    && it.vrfVerificationKey == null
                    && it.signature == null
                    && it.expirationTime.isAfter(OffsetDateTime.now())
            }
            .verifyComplete()

        verify { stakepoolVerificationRepository wasNot Called } // verifications are only persisted in Mongo after confirmation
    }

    @Test
    fun `starting a stake pool verification gives the right error if no valid pool is found for hash`() {
        every { cip22Dao.getVrfVerificationKeyHashForPool(any()) } answers { Mono.error(NoSuchElementException()) }

        val result = service.startStakepoolVerification("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b")

        StepVerifier.create(result)
            .expectErrorMatches { it is InvalidCip22Verification }
            .verify()

        verify { stakepoolVerificationRepository wasNot Called } // verifications are only persisted in Mongo after confirmation
    }

    @Test
    @OptIn(ExperimentalStdlibApi::class)
    fun `stake pool verification should return success when all conditions are met, but not finalize by default`() {
        val stakepoolVerification = makeStakepoolVerificationDto(OffsetDateTime.now())
        every { opsForValue.get("cip-0022:${stakepoolVerification.poolHash}") } returns stakepoolVerification
        every { cip22Dao.getVrfVerificationKeyHashForPool(any()) } answers { Mono.just("a".repeat(64).toByteArray()) }
        every { libSodiumService.cryptoBlake2bHash(any(), any()) } returns "61616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161".hexToByteArray()
        every { libSodiumService.cryptoVrfProofToHash_ietfdraft03(any()) } returns ByteArray(32)
        every { libSodiumService.cryptoVrfVerify_ietfdraft03(any(), any(), any()) } returns ByteArray(32)

        val result = service.doStakepoolVerification(stakepoolVerification.poolHash, stakepoolVerification.nonce, stakepoolVerification)

        StepVerifier.create(result)
            .expectNext(stakepoolVerification)
            .verifyComplete()

        verify { stakepoolVerificationRepository wasNot Called }
    }

    @Test
    @OptIn(ExperimentalStdlibApi::class)
    fun `stake pool verification should return success and store verification and remove redis cache entry`() {
        val stakepoolVerification = makeStakepoolVerificationDto(OffsetDateTime.now())
        every { opsForValue.get("cip-0022:${stakepoolVerification.poolHash}") } returns stakepoolVerification
        every { cip22Dao.getVrfVerificationKeyHashForPool(any()) } answers { Mono.just("a".repeat(64).toByteArray()) }
        every { libSodiumService.cryptoBlake2bHash(any(), any()) } returns "61616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161".hexToByteArray()
        every { libSodiumService.cryptoVrfProofToHash_ietfdraft03(any()) } returns ByteArray(32)
        every { libSodiumService.cryptoVrfVerify_ietfdraft03(any(), any(), any()) } returns ByteArray(32)
        every { stakepoolVerificationRepository.save(any()) } answers { Mono.just(firstArg()) }
        every { redisTemplate.delete("cip-0022:${stakepoolVerification.poolHash}") } returns true

        val result = service.doStakepoolVerification(stakepoolVerification.poolHash, stakepoolVerification.nonce, stakepoolVerification, true)

        StepVerifier.create(result)
            .expectNext(stakepoolVerification)
            .verifyComplete()

        verify(exactly = 1) { stakepoolVerificationRepository.save(any()) }
        verify(exactly = 1) { redisTemplate.delete("cip-0022:${stakepoolVerification.poolHash}") }
    }

    @Test
    fun `stake pool verification should return error when vrfVerificationKey is null`() {
        val stakepoolVerification = makeStakepoolVerificationDto(OffsetDateTime.now()).copy(vrfVerificationKey = null)
        val result = service.doStakepoolVerification(stakepoolVerification.poolHash, stakepoolVerification.nonce, stakepoolVerification)

        StepVerifier.create(result)
            .expectErrorMatches { it is InvalidCip22Verification && it.message == "VRF Verification Key and Signature must be provided for verification" }
            .verify()

        verify { stakepoolVerificationRepository wasNot Called }
    }

    @Test
    fun `stake pool verification should return error when signature is null`() {
        val stakepoolVerification = makeStakepoolVerificationDto(OffsetDateTime.now()).copy(signature = null)
        val result = service.doStakepoolVerification(stakepoolVerification.poolHash, stakepoolVerification.nonce, stakepoolVerification)

        StepVerifier.create(result)
            .expectErrorMatches { it is InvalidCip22Verification && it.message == "VRF Verification Key and Signature must be provided for verification" }
            .verify()

        verify { stakepoolVerificationRepository wasNot Called }
    }

    @Test
    fun `stake pool verification should return error when verification for this pool cannot be found`() {
        val stakepoolVerification = makeStakepoolVerificationDto(OffsetDateTime.now())
        every { opsForValue.get("cip-0022:${stakepoolVerification.poolHash}") } returns null
        val result = service.doStakepoolVerification(stakepoolVerification.poolHash, stakepoolVerification.nonce, stakepoolVerification)

        StepVerifier.create(result)
            .expectErrorMatches { it is ExpiredCip22Verification && it.message == "Verification with nonce ${stakepoolVerification.nonce} expired" }
            .verify()

        verify { stakepoolVerificationRepository wasNot Called }
    }

    @Test
    @OptIn(ExperimentalStdlibApi::class)
    fun `stake pool verification should return error if no valid CBOR hex provided in VRF verification key`() {
        val stakepoolVerification = makeStakepoolVerificationDto(OffsetDateTime.now()).copy(vrfVerificationKey = VrfVerificationKey("a", "b", ObjectMapper(CBORFactory()).writeValueAsBytes("notbinary").toHexString()))
        every { opsForValue.get("cip-0022:${stakepoolVerification.poolHash}") } returns stakepoolVerification
        val result = service.doStakepoolVerification(stakepoolVerification.poolHash, stakepoolVerification.nonce, stakepoolVerification)

        StepVerifier.create(result)
            .expectErrorMatches { it is InvalidCip22Verification && it.message == "VRF Verification Key must be a CBOR encoded binary" }
            .verify()

        verify { stakepoolVerificationRepository wasNot Called }
    }

    @Test
    @OptIn(ExperimentalStdlibApi::class)
    fun `stake pool verification should return error if VRF key length is not appropriate`() {
        val stakepoolVerification = makeStakepoolVerificationDto(OffsetDateTime.now()).copy(vrfVerificationKey = VrfVerificationKey("VRFSigningKey_PraosVRF", "VRF Signing Key", ObjectMapper(CBORFactory()).writeValueAsBytes(ByteArray(36)).toHexString()))
        every { opsForValue.get("cip-0022:${stakepoolVerification.poolHash}") } returns stakepoolVerification
        every { cip22Dao.getVrfVerificationKeyHashForPool(any()) } answers { Mono.just("a".repeat(64).toByteArray()) }

        val result = service.doStakepoolVerification(stakepoolVerification.poolHash, stakepoolVerification.nonce, stakepoolVerification)

        StepVerifier.create(result)
            .expectErrorMatches { it is InvalidCip22Verification && it.message?.contains("you have likely provided a signing key") ?: false }
            .verify()

        verify { stakepoolVerificationRepository wasNot Called }
    }

    @Test
    @OptIn(ExperimentalStdlibApi::class)
    fun `stake pool verification should return error if signature is incorrect`() {
        val stakepoolVerification = makeStakepoolVerificationDto(OffsetDateTime.now())
        every { opsForValue.get("cip-0022:${stakepoolVerification.poolHash}") } returns stakepoolVerification
        every { cip22Dao.getVrfVerificationKeyHashForPool(any()) } answers { Mono.just("a".repeat(64).toByteArray()) }
        every { libSodiumService.cryptoBlake2bHash(any(), any()) } returns "61616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161616161".hexToByteArray()
        every { libSodiumService.cryptoVrfProofToHash_ietfdraft03(any()) } returns ByteArray(34)
        every { libSodiumService.cryptoVrfVerify_ietfdraft03(any(), any(), any()) } returns ByteArray(32)

        val result = service.doStakepoolVerification(stakepoolVerification.poolHash, stakepoolVerification.nonce, stakepoolVerification)

        StepVerifier.create(result)
            .expectErrorMatches { it is InvalidCip22Verification && it.message == "Failed to complete stakepool verification: Verification failed as the signature did not match the VRF Verification Key" }
            .verify()

        verify { stakepoolVerificationRepository wasNot Called }
    }

    @Test
    fun `should save verification and delete from Redis when finalizing`() {


    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun makeStakepoolVerificationDto(now: OffsetDateTime) = StakepoolVerificationDto(
        nonce = "a".repeat(64),
        domain = "ryp.io",
        poolHash = "be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4be80794a946cf5e578846fc81e3c62ac13f4ab3335e0f5dc046edad4",
        vrfVerificationKey = VrfVerificationKey(
            type = "VrfVerificationKey_PraosVRF",
            description = "VRF Verification Key",
            cborHex = ObjectMapper(CBORFactory()).writeValueAsBytes(ByteArray(32)).toHexString()
        ),
        signature = "a".repeat(128),
        createTime = now,
        expirationTime = now.plusMinutes(2)
    )

}
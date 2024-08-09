package io.vibrantnet.ryp.core.verification.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.*
import io.ryp.cardano.model.StakepoolDetailsDto
import io.vibrantnet.ryp.core.verification.CoreVerificationConfiguration
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
    private val cip22Dao = mockk<Cip22Dao>()
    private val stakepoolDao = mockk<StakepoolDao>()
    private val opsForValue = mockk<ValueOperations<String, Any>>()
    private val redisTemplate = mockk<RedisTemplate<String, Any>>()
    private val stakepoolVerificationRepository = mockk<StakepoolVerificationRepository>()
    private val objectMapper = jacksonObjectMapper()
    private val config = CoreVerificationConfiguration(ipfslink = "", blockfrost = null)
    private val service = PoolsApiServiceVibrant(
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

}
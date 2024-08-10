package io.vibrantnet.ryp.core.subscription.service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.*
import io.ryp.cardano.model.StakepoolDetailsDto
import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import io.vibrantnet.ryp.core.loadJsonFromResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.util.concurrent.TimeUnit

internal class VerifyServiceVibrantTest {
    @Test
    fun `getting the policies of a wallet works and caches the information accordingly`() {
        val tokensInWalletJson = loadJsonFromResource("sample-json/test-verify-service-wallet-response.json")
        mockBackend.enqueue(
            MockResponse().setResponseCode(200).setBody(tokensInWalletJson).addHeader("Content-Type", "application/json")
        )
        val redisTemplate = mockk<RedisTemplate<String, Any>>()
        val opsForList = mockk<ListOperations<String, Any>>()
        every { redisTemplate.opsForList() } returns opsForList
        every { opsForList.range("stakeAddress:assetcounts:123", 0, -1) } returns null
        every { opsForList.rightPushAll("stakeAddress:assetcounts:123", any<TokenOwnershipInfoWithAssetCount>()) } returns 2
        every { redisTemplate.expire("stakeAddress:assetcounts:123", any(), any()) } returns true
        val verifyService = VerifyServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build(),
            redisTemplate,
            jacksonObjectMapper()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
        )
        val result = verifyService.getPoliciesInWallet("123")

        StepVerifier.create(result)
            .expectNext(TokenOwnershipInfoWithAssetCount("123", "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f", 1))
            .expectNext(TokenOwnershipInfoWithAssetCount("123", "2d01b3496fd22b1a61e6227c27250225b1186e5ebae7360b1fc5392c", 2))
            .verifyComplete()

        verify(exactly = 2) {
            opsForList.rightPushAll("stakeAddress:assetcounts:123", any<TokenOwnershipInfoWithAssetCount>())
        }
        verify(exactly = 1) {
            redisTemplate.expire("stakeAddress:assetcounts:123",  10, TimeUnit.MINUTES) // Exists to ensure we do not cache unreasonably long
        }
    }

    @Test
    fun `getting the policies uses the cache if possible`() {
        val redisTemplate = mockk<RedisTemplate<String, Any>>()
        val opsForList = mockk<ListOperations<String, Any>>()
        every { redisTemplate.opsForList() } returns opsForList
        every { opsForList.range("stakeAddress:assetcounts:123", 0, -1) } returns listOf(
            TokenOwnershipInfoWithAssetCount("123", "policy1", 1),
            TokenOwnershipInfoWithAssetCount("123", "policy2", 2)
        )
        val verifyService = VerifyServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build(),
            redisTemplate,
            jacksonObjectMapper()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
        )
        val result = verifyService.getPoliciesInWallet("123")

        StepVerifier.create(result)
            .expectNext(TokenOwnershipInfoWithAssetCount("123", "policy1", 1))
            .expectNext(TokenOwnershipInfoWithAssetCount("123", "policy2", 2))
            .verifyComplete()

        verify(exactly = 0) {
            opsForList.rightPushAll("stakeAddress:assetcounts:123", any<TokenOwnershipInfoWithAssetCount>())
        }
        verify(exactly = 0) {
            redisTemplate.expire("stakeAddress:assetcounts:123", any())
        }

    }

    @Test
    fun `getting stake pool details works and caches the information accordingly`() {
        val stakePoolDetailsJson = loadJsonFromResource("sample-json/test-verify-service-stakepool-response.json")
        mockBackend.enqueue(
            MockResponse().setResponseCode(200).setBody(stakePoolDetailsJson).addHeader("Content-Type", "application/json")
        )
        val redisTemplate = mockk<RedisTemplate<String, Any>>()
        val opsForValue = mockk<ValueOperations<String, Any>>()
        every { redisTemplate.opsForValue() } returns opsForValue
        every { opsForValue.get("stakeAddress:pool:123") } returns null
        every { opsForValue.set("stakeAddress:pool:123", any(), 10, TimeUnit.MINUTES) } just Runs
        val verifyService = VerifyServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build(),
            redisTemplate,
            jacksonObjectMapper()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
        )
        val result = verifyService.getStakepoolDetailsForStakeAddress("123")

        val stakepoolDetails = StakepoolDetailsDto(
            poolHash = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
            ticker = "VIBRN",
            name = "Vibrant",
            homepage = "https://vibrantnet.io",
            description = "Vibrant Stake Pool"
        )

        StepVerifier.create(result)
            .expectNext(stakepoolDetails)
            .verifyComplete()

        verify(exactly = 1) {
            opsForValue.set("stakeAddress:pool:123", stakepoolDetails, 10, TimeUnit.MINUTES)
        }
    }

    @Test
    fun `getting the stake pool uses the cache if possible`() {
        val stakepoolDetails = StakepoolDetailsDto(
            poolHash = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
            ticker = "VIBRN",
            name = "Vibrant",
            homepage = "https://vibrantnet.io",
            description = "Vibrant Stake Pool"
        )

        val redisTemplate = mockk<RedisTemplate<String, Any>>()
        val opsForValue = mockk<ValueOperations<String, Any>>()
        every { redisTemplate.opsForValue() } returns opsForValue
        every { opsForValue.get("stakeAddress:pool:123") } returns stakepoolDetails
        val verifyService = VerifyServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build(),
            redisTemplate,
            jacksonObjectMapper()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
        )
        val result = verifyService.getStakepoolDetailsForStakeAddress("123")

        StepVerifier.create(result)
            .expectNext(stakepoolDetails)
            .verifyComplete()

        verify(exactly = 0) {
            opsForValue.set("stakeAddress:pool:123", any(), any(), any())
        }
    }

    @Test
    fun `getting a stake pool that does not exist returns an empty Mono`() {
        mockBackend.enqueue(MockResponse().setResponseCode(404))
        val redisTemplate = mockk<RedisTemplate<String, Any>>()
        val opsForValue = mockk<ValueOperations<String, Any>>()
        every { redisTemplate.opsForValue() } returns opsForValue
        every { opsForValue.get("stakeAddress:pool:123") } returns null
        val verifyService = VerifyServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build(),
            redisTemplate,
            jacksonObjectMapper()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
        )
        val result = verifyService.getStakepoolDetailsForStakeAddress("123")

        StepVerifier.create(result)
            .verifyComplete()

    }

    companion object {
        lateinit var mockBackend: MockWebServer

        @JvmStatic
        @BeforeAll
        fun setUpClass() {
            mockBackend = MockWebServer()
            mockBackend.start()
        }

        @JvmStatic
        @AfterAll
        fun tearDownClass() {
            mockBackend.shutdown()
        }


    }
}
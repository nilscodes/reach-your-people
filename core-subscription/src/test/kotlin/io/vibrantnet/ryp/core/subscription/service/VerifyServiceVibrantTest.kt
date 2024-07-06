package io.vibrantnet.ryp.core.subscription.service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.ryp.shared.model.TokenOwnershipInfoWithAssetCount
import io.vibrantnet.ryp.core.loadJsonFromResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.time.Duration
import java.time.temporal.ChronoUnit

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
        every { opsForList.range("stakeAddress:123", 0, -1) } returns null
        every { opsForList.rightPushAll("stakeAddress:123", any<TokenOwnershipInfoWithAssetCount>()) } returns 2
        every { redisTemplate.expire("stakeAddress:123", any()) } returns true
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

        verify(exactly = 2) {
            opsForList.rightPushAll("stakeAddress:123", any<TokenOwnershipInfoWithAssetCount>())
        }
        verify(exactly = 1) {
            redisTemplate.expire("stakeAddress:123", Duration.of(10, ChronoUnit.MINUTES)) // Exists to ensure we do not cache unreasonably long
        }
    }

    @Test
    fun `getting the policies uses the cache if possible`() {
        val redisTemplate = mockk<RedisTemplate<String, Any>>()
        val opsForList = mockk<ListOperations<String, Any>>()
        every { redisTemplate.opsForList() } returns opsForList
        every { opsForList.range("stakeAddress:123", 0, -1) } returns listOf(
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
            opsForList.rightPushAll("stakeAddress:123", any<TokenOwnershipInfoWithAssetCount>())
        }
        verify(exactly = 0) {
            redisTemplate.expire("stakeAddress:123", Duration.of(10, ChronoUnit.MINUTES))
        }

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
package io.vibrantnet.ryp.vibrant.service

import io.vibrantnet.ryp.vibrant.loadJsonFromResource
import io.vibrantnet.ryp.vibrant.model.BlockchainType
import io.vibrantnet.ryp.vibrant.model.VerificationDto
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.util.*

internal class VibrantCommunityServiceTest {
    @Test
    fun `getting verifications for an existing account works`() {
        val fixedDate = Date.from(OffsetDateTime.parse("2024-07-06T12:00:00Z").toInstant())
        val externalAccountResponse = loadJsonFromResource("sample-json/test-get-external-account-by-discord-id.json")
        val verificationsResponse = loadJsonFromResource("sample-json/test-get-verifications-for-discord-id.json")
        mockBackend.enqueue(MockResponse().setBody(externalAccountResponse).addHeader("Content-Type", "application/json"))
        mockBackend.enqueue(MockResponse().setBody(verificationsResponse).addHeader("Content-Type", "application/json"))
        val communityService = VibrantCommunityService(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build()
        )

        val result = communityService.getVerificationsForDiscordUserId(123L)

        StepVerifier.create(result)
            .expectNext(VerificationDto(1, 1000000, BlockchainType.CARDANO, "addr1123", "stake1_test", "jbfw", fixedDate, fixedDate, true, fixedDate, false, null),)
            .verifyComplete()
    }

    @Test
    fun `getting verifications shows an appropriate error if external account does not exist`() {
        mockBackend.enqueue(MockResponse().setResponseCode(404).addHeader("Content-Type", "application/json"))
        val communityService = VibrantCommunityService(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build()
        )

        val result = communityService.getVerificationsForDiscordUserId(123L)

        StepVerifier.create(result)
            .expectError(NoSuchElementException::class.java)
            .verify()

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
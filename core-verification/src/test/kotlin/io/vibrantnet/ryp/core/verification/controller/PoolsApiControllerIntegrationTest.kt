package io.vibrantnet.ryp.core.verification.controller

import io.mockk.every
import io.mockk.mockk
import io.ryp.cardano.model.stakepools.StakepoolVerificationDto
import io.ryp.cardano.model.stakepools.VrfVerificationKey
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.verification.service.PoolsApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@WebFluxTest(
    controllers = [PoolsApiController::class, ApiExceptionHandler::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
class PoolsApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun poolsApiService() = mockk<PoolsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var poolsApiService: PoolsApiService

    @Test
    fun `starting a stake pool verification works`() {
        val stakepoolVerification = makeStakepoolVerificationDto()
        every { poolsApiService.startStakepoolVerification(stakepoolVerification.poolHash) } answers {
            Mono.just(stakepoolVerification)
        }

        val responseJson = loadJsonFromResource("sample-json/test-start-stakepool-verification-response.json")

        webClient.post()
            .uri("/pools/${stakepoolVerification.poolHash}/verifications")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectHeader()
            .valueEquals("Location", "/pools/${stakepoolVerification.poolHash}/verifications/${stakepoolVerification.nonce}")
            .expectBody().json(responseJson)
    }

    @Test
    fun `testing a stake pool verification works`() {
        val stakepoolVerification = makeStakepoolVerificationDto().copy(
            vrfVerificationKey = VrfVerificationKey(
                type = "VRFVerificationKey",
                description = "irrelevant",
                cborHex = "00"
            ),
            signature = "signature"
        )
        every { poolsApiService.testStakepoolVerification(stakepoolVerification.poolHash, stakepoolVerification.nonce, stakepoolVerification) } answers {
            Mono.just(stakepoolVerification)
        }

        val requestJson = loadJsonFromResource("sample-json/test-test-stakepool-verification-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-test-stakepool-verification-response.json")

        webClient.post()
            .uri("/pools/${stakepoolVerification.poolHash}/verifications/${stakepoolVerification.nonce}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestJson)
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `completing a stake pool verification works`() {
        val stakepoolVerification = makeStakepoolVerificationDto().copy(
            vrfVerificationKey = VrfVerificationKey(
                type = "VRFVerificationKey",
                description = "irrelevant",
                cborHex = "00"
            ),
            signature = "signature"
        )
        every { poolsApiService.completeStakepoolVerification(stakepoolVerification.poolHash, stakepoolVerification.nonce, stakepoolVerification) } answers {
            Mono.just(stakepoolVerification)
        }

        val requestJson = loadJsonFromResource("sample-json/test-complete-stakepool-verification-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-complete-stakepool-verification-response.json")

        webClient.post()
            .uri("/pools/${stakepoolVerification.poolHash}/verifications/${stakepoolVerification.nonce}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestJson)
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    private fun makeStakepoolVerificationDto() = StakepoolVerificationDto(
        nonce = "a".repeat(64),
        domain = "ryp.io",
        poolHash = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
        vrfVerificationKey = null,
        signature = null,
        createTime = OffsetDateTime.parse("2021-01-01T00:00:00Z"),
        expirationTime = OffsetDateTime.parse("2021-01-01T00:10:00Z")
    )
}
package io.vibrantnet.ryp.core.points.controller

import io.mockk.every
import io.mockk.mockk
import io.ryp.shared.model.points.PointsClaimDto
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.points.model.DuplicatePointsClaimException
import io.vibrantnet.ryp.core.points.model.PointsSummaryDto
import io.vibrantnet.ryp.core.points.service.PointsApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@WebFluxTest(
    controllers = [PointsApiController::class, ApiExceptionHandler::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class]
)
class PointsApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun pointsApiService() = mockk<PointsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var pointsApiService: PointsApiService

    @Test
    fun `creating a points claim works`() {
        val claimId = "signup-17"
        val pointClaim = makePointClaimDto(claimId)
        every { pointsApiService.createPointClaim(pointClaim.accountId, pointClaim.tokenId, claimId, any()) } answers {
            Mono.just(pointClaim)
        }

        val requestJson = loadJsonFromResource("sample-json/test-create-point-claim-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-create-point-claim-response.json")

        webClient.post()
            .uri("/points/accounts/${pointClaim.accountId}/claims/${pointClaim.tokenId}/$claimId")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isCreated
            .expectHeader()
            .valueEquals("Location", "/points/accounts/${pointClaim.accountId}/claims/${pointClaim.tokenId}/$claimId")
            .expectBody().json(responseJson)
    }

    @Test
    fun `creating a point claim that already exists show the correct conflict response`() {
        val claimId = "signup-17"
        val pointClaim = makePointClaimDto(claimId)
        every { pointsApiService.createPointClaim(pointClaim.accountId, pointClaim.tokenId, claimId, any()) } answers {
            Mono.error(DuplicatePointsClaimException("Claim with ID $claimId already exists"))
        }

        val requestJson = loadJsonFromResource("sample-json/test-create-point-claim-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-create-point-claim-conflict-response.json")

        webClient.post()
            .uri("/points/accounts/${pointClaim.accountId}/claims/${pointClaim.tokenId}/$claimId")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting all point claims for an account works`() {
        val accountId = 12L
        every { pointsApiService.getPointClaimsForAccount(accountId) } answers {
            Flux.fromIterable(
                listOf(
                    makePointClaimDto("signup-17"),
                    makePointClaimDto("referral-19"),
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-all-point-claims-account-response.json")

        webClient.get()
            .uri("/points/accounts/$accountId/claims")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting all point claims for an account and a specific token works`() {
        val accountId = 12L
        val tokenId = 18
        every { pointsApiService.getPointClaimsForAccountAndToken(accountId, tokenId) } answers {
            Flux.fromIterable(
                listOf(
                    makePointClaimDto("signup-17"),
                    makePointClaimDto("referral-19"),
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-all-point-claims-account-token-response.json")

        webClient.get()
            .uri("/points/accounts/$accountId/claims/$tokenId")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `the point summary for an account is serialized is correct`() {
        val accountId = 12L
        every { pointsApiService.getPointsSummaryForAccount(accountId) } answers {
            Flux.fromIterable(
                listOf(
                    PointsSummaryDto(
                        tokenId = 99,
                        totalPointsClaimed = 100,
                        totalPointsAvailable = 200,
                        totalPointsSpent = 50,
                        totalPointsClaimable = 150,
                    ), PointsSummaryDto(
                        tokenId = 18,
                        totalPointsClaimed = 1,
                        totalPointsAvailable = 2,
                        totalPointsSpent = 3,
                        totalPointsClaimable = 4,
                    )
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-point-summary-response.json")

        webClient.get()
            .uri("/points/accounts/$accountId")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `get a specific point claim by ID works`() {
        val accountId = 12L
        val tokenId = 18
        val claimId = "signup-12"
        every { pointsApiService.getSpecificPointClaimForAccountAndToken(accountId, tokenId, claimId) } answers {
            Mono.just(makePointClaimDto(claimId))
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-specific-point-claim-response.json")

        webClient.get()
            .uri("/points/accounts/$accountId/claims/$tokenId/$claimId")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `updating a specific point claim by ID works when just updating claim status`() {
        val accountId = 12L
        val tokenId = 18
        val claimId = "signup-12"
        every { pointsApiService.updatePointClaim(accountId, tokenId, claimId, any()) } answers {
            Mono.just(makePointClaimDto(claimId).copy(
                claimed = true,
                claimTime = OffsetDateTime.parse("2024-02-01T00:00:00Z")
            ))
        }

        val requestJson = loadJsonFromResource("sample-json/test-update-point-claim-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-update-point-claim-response.json")

        webClient.patch()
            .uri("/points/accounts/$accountId/claims/$tokenId/$claimId")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `updating a specific point claim by ID works when just updating expiration time`() {
        val accountId = 12L
        val tokenId = 18
        val claimId = "signup-12"
        every { pointsApiService.updatePointClaim(accountId, tokenId, claimId, any()) } answers {
            Mono.just(makePointClaimDto(claimId).copy(
                expirationTime = OffsetDateTime.parse("2024-02-01T00:00:00Z")
            ))
        }

        val requestJson = loadJsonFromResource("sample-json/test-update-point-claim-expiration-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-update-point-claim-expiration-response.json")

        webClient.patch()
            .uri("/points/accounts/$accountId/claims/$tokenId/$claimId")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    private fun makePointClaimDto(claimId: String) = PointsClaimDto(
        claimId = claimId,
        category = "signup",
        tokenId = 18,
        accountId = 12,
        points = 100,
        projectId = 119,
        claimed = false,
        claimTime = null,
        createTime = OffsetDateTime.parse("2021-01-01T00:00:00Z"),
        expirationTime = OffsetDateTime.parse("2025-02-01T00:00:00Z"),
    )
}
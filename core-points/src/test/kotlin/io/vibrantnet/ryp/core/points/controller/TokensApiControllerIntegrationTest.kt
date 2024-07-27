package io.vibrantnet.ryp.core.points.controller

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.points.model.PointsTokenDto
import io.vibrantnet.ryp.core.points.service.TokensApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@WebFluxTest(
    controllers = [TokensApiController::class, ApiExceptionHandler::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class]
)
class TokensApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun tokensApiService() = mockk<TokensApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var tokensApiService: TokensApiService

    @Test
    fun `creating a token works`() {
        val tokenId = 17
        every { tokensApiService.createPointsToken(any()) } answers {
            Mono.just(makeToken(17))
        }

        val requestJson = loadJsonFromResource("sample-json/test-create-token-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-create-token-response.json")

        webClient.post()
            .uri("/tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().valueEquals("Location", "/tokens/$tokenId")
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting a token by ID works`() {
        val tokenId = 21
        every { tokensApiService.getPointsToken(tokenId) } answers {
            Mono.just(makeToken(tokenId))
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-token-response.json")

        webClient.get()
            .uri("/tokens/$tokenId")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `listing all tokens works`() {
        val tokenIds = listOf(1, 2, 3)
        every { tokensApiService.listPointsTokens() } answers {
            Flux.fromIterable(
                listOf(
                    makeToken(tokenIds[0]),
                    makeToken(tokenIds[1]),
                    makeToken(tokenIds[2])
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-list-tokens-response.json")

        webClient.get()
            .uri("/tokens")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    private fun makeToken(tokenId: Int) = PointsTokenDto(
        id = tokenId,
        creator = 39,
        name = "TST",
        displayName = "A test token",
        projectId = 172L,
        createTime = OffsetDateTime.parse("2021-01-01T00:00:00Z"),
        modifyTime = OffsetDateTime.parse("2021-02-01T00:00:00Z"),
    )
}
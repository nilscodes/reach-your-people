package io.vibrantnet.ryp.core.subscription.controller

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.ryp.shared.model.ExternalAccountDto
import io.vibrantnet.ryp.core.subscription.service.ExternalAccountsApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@WebFluxTest(controllers = [ExternalAccountsApiController::class, ApiExceptionHandler::class])
class ExternalAccountsApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean fun externalAccountsApiService() = mockk<ExternalAccountsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var externalAccountsApiService: ExternalAccountsApiService

    @Test
    fun `create external account works with correct payload`() {
        every { externalAccountsApiService.createExternalAccount(any()) } answers {
            Mono.just(
                ExternalAccountDto(
                id = 14,
                referenceId = firstArg<ExternalAccountDto>().referenceId,
                referenceName = firstArg<ExternalAccountDto>().referenceName,
                type = firstArg<ExternalAccountDto>().type,
                registrationTime = OffsetDateTime.parse("2021-08-01T00:00:00Z"),
            )
            )
        }

        val requestJson = loadJsonFromResource("sample-json/test-create-externalaccount-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-create-externalaccount-response.json")

        webClient.post()
            .uri("/externalaccounts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().valueEquals("Location", "/externalaccounts/14")
            .expectBody().json(responseJson)
    }

    @Test
    fun `create external account fails validation with good exception body when reference ID is missing`() {
        val requestJson = loadJsonFromResource("sample-json/test-create-externalaccount-request.json")
            .replace("\"referenceId\": \"123\",", "")

        webClient.post()
            .uri("/externalaccounts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `create external account fails validation with good exception body when type is missing`() {
        val requestJson = loadJsonFromResource("sample-json/test-create-externalaccount-notype-request.json")

        webClient.post()
            .uri("/externalaccounts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `get account by provider type and reference ID works`() {
        every { externalAccountsApiService.findExternalAccountByProviderAndReferenceId("discord", "123") } answers {
            Mono.just(
                ExternalAccountDto(
                id = 14,
                referenceId = "123",
                referenceName = "Test",
                type = "DISCORD_SOUP",
                registrationTime = OffsetDateTime.parse("2021-08-01T00:00:00Z"),
            )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-create-externalaccount-response.json")

        webClient.get()
            .uri("/externalaccounts/discord/123")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }
}
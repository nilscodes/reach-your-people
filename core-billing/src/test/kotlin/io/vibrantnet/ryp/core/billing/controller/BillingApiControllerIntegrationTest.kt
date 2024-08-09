package io.vibrantnet.ryp.core.billing.controller

import io.mockk.every
import io.mockk.mockk
import io.ryp.core.billing.model.BillDto
import io.ryp.core.billing.model.Currency
import io.ryp.core.billing.model.OrderDto
import io.ryp.core.billing.model.OrderItemDto
import io.vibrantnet.ryp.core.billing.service.BillingApiService
import io.vibrantnet.ryp.core.loadJsonFromResource
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
    controllers = [BillingApiController::class, ApiExceptionHandler::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class]
)
class BillingApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun billingApiService() = mockk<BillingApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var billingApiService: BillingApiService

    @Test
    fun `creating a bill works`() {
        val billDto = makeBillDto()
        every { billingApiService.createBill(billDto.accountId, any()) } answers {
            Mono.just(billDto)
        }

        val requestJson = loadJsonFromResource("sample-json/test-create-bill-request.json")
        val responseJson = loadJsonFromResource("sample-json/test-create-bill-response.json")

        webClient.post()
            .uri("/billing/accounts/${billDto.accountId}")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestJson))
            .exchange()
            .expectStatus().isCreated
            .expectHeader()
            .valueEquals("Location", "/billing/accounts/${billDto.accountId}/${billDto.id}")
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting bills for an account works`() {
        val billDto = makeBillDto()
        every { billingApiService.getBillsForAccount(billDto.accountId) } answers {
            Flux.fromIterable(listOf(billDto))
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-bills-for-account-response.json")

        webClient.get()
            .uri("/billing/accounts/${billDto.accountId}")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    private fun makeBillDto(amountReceived: Long? = null) = BillDto(
        accountId = 12,
        createTime = OffsetDateTime.parse("2021-01-01T00:00:00Z"),
        id = 209,
        channel = "cardano",
        currency = Currency.LOVELACE_ADA,
        order = OrderDto(
            items = listOf(
                OrderItemDto("stuff", 17)
            )
        ),
        amountRequested = 25000000L,
        amountReceived = amountReceived,
        paymentProcessedTime = null,
    )
}
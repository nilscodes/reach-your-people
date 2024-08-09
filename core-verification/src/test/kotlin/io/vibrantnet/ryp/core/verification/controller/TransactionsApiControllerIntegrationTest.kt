package io.vibrantnet.ryp.core.verification.controller

import io.mockk.every
import io.mockk.mockk
import io.ryp.cardano.model.TransactionSummaryDto
import io.ryp.cardano.model.TxOutputSummaryDto
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.verification.service.TransactionsApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@WebFluxTest(
    controllers = [TransactionsApiController::class, ApiExceptionHandler::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
class TransactionsApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun transactionsApiService() = mockk<TransactionsApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var transactionsApiService: TransactionsApiService

    @Test
    fun `getting a basic transaction summary works`() {
        val transactionHash = "9f83e5484f543e05b52e99988272a31da373f3aab4c064c76db96643a355d9dc"
        every { transactionsApiService.getTransactionSummary(transactionHash) } answers {
            Mono.just(
                TransactionSummaryDto(
                    transactionHash,
                    OffsetDateTime.parse("2021-07-01T00:00:00Z"),
                    listOf(TxOutputSummaryDto(
                        "addr1itsyou",
                        25000000L,
                    ))
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-transaction-summary-response.json")

        webClient.get()
            .uri("/transactions/${transactionHash}/summary")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }
}
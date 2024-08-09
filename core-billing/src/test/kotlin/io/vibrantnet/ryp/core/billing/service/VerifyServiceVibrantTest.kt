package io.vibrantnet.ryp.core.billing.service

import io.ryp.cardano.model.TransactionSummaryDto
import io.ryp.cardano.model.TxOutputSummaryDto
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.time.OffsetDateTime

const val txSummaryPayload = """
    {
        "transactionHash": "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
        "transactionTime": "2021-01-02T03:04:05Z",
        "outputs": [
            {
                "address": "addr1itsyou",
                "lovelace": 123456
            }
        ]
    }
"""

internal class VerifyServiceVibrantTest {
    @Test
    fun `valid transaction summary returned from verify service`() {
        mockBackend.enqueue(
            MockResponse().setResponseCode(200).setBody(txSummaryPayload).addHeader("Content-Type", "application/json")
        )
        val verifyService = VerifyServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build()
        )
        val result = verifyService.getTransactionSummary("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b")
        StepVerifier.create(result)
            .expectNext(TransactionSummaryDto(
                "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
                OffsetDateTime.parse("2021-01-02T03:04:05Z"),
                listOf(
                    TxOutputSummaryDto("addr1itsyou", 123456L)
                )
            ))
            .verifyComplete()
    }

    @Test
    fun `missing transaction via 404 does return the right error`() {
        mockBackend.enqueue(MockResponse().setResponseCode(404))
        val verifyService = VerifyServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build()
        )
        val result = verifyService.getTransactionSummary("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b")
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
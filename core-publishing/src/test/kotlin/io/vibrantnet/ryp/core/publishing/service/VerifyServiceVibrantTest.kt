package io.vibrantnet.ryp.core.publishing.service

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

internal class VerifyServiceVibrantTest {

    @Test
    fun `valid boolean returned from verification endpoint is properly processed`() {
        mockBackend.enqueue(
            MockResponse().setResponseCode(200).setBody("true").addHeader("Content-Type", "application/json")
        )
        val verifyService = VerifyServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build()
        )
        val result = verifyService.verifyCip66("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b", "service", "reference")
        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `a not found response for verification data is the same as the verification returning false`() {
        mockBackend.enqueue(MockResponse().setResponseCode(404))
        val verifyService = VerifyServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build()
        )
        val result = verifyService.verifyCip66("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b", "service", "reference")
        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun `any non 404 response code is propagated as an error`() {
        mockBackend.enqueue(MockResponse().setResponseCode(500))
        val verifyService = VerifyServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build()
        )
        val result = verifyService.verifyCip66("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b", "service", "reference")
        StepVerifier.create(result)
            .expectError()
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

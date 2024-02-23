package io.vibrantnet.ryp.core.verification.configuration

import io.vibrantnet.ryp.core.verification.BlockfrostConfig
import io.vibrantnet.ryp.core.verification.CoreVerificationConfiguration
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class BlockfrostConfigurationTest {
    @Test
    fun blockfrostClient() {
        mockBackend.enqueue(MockResponse().setBody("Hello, World!").addHeader("Content-Type", "text/plain; charset=utf-8"))
        val blockfrostConfiguration = BlockfrostConfiguration(
            CoreVerificationConfiguration("blockfrost", "", BlockfrostConfig(String.format("http://localhost:%s", mockBackend.port), "1234567890")))
        val blockfrostClient = blockfrostConfiguration.blockfrostClient()
        val response = blockfrostClient.get().uri("/").retrieve().bodyToMono(String::class.java).block()
        assertEquals("Hello, World!", response)
        val request = mockBackend.takeRequest()
        assertEquals("1234567890", request.getHeader("project_id"))
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
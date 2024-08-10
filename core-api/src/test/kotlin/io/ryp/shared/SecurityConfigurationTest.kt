package io.ryp.shared

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SecurityConfigurationTest {
    @Test
    fun `test client with API key`() {
        testApiKeyForWebClient("api-key")
    }

    @Test
    fun `test client without API key`() {
        testApiKeyForWebClient(null)
    }

    private fun testApiKeyForWebClient(apiKey: String?) {
        val securityConfiguration = SecurityConfiguration(apiKey = apiKey)
        var capturedHeaders: MutableMap<String, String>? = null
        val builder = createCoreServiceWebClientBuilder("http://localhost:8080", securityConfiguration.apiKey)
            .filter { request, next ->
                capturedHeaders = request.headers().toSingleValueMap()
                next.exchange(request)
            }

        val webClient = builder.build()
        try {
            webClient.get()
                .uri("/")
                .retrieve()
                .toBodilessEntity()
                .block()
        } catch (ex: Exception) {
            // Ignore exceptions (e.g., host not found)
        }

        val actualApiKey: String? = capturedHeaders?.get("Authorization")
        assertEquals(securityConfiguration.apiKey, actualApiKey)
    }
}
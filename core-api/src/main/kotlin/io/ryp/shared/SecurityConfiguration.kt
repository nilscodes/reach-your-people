package io.ryp.shared

import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

data class SecurityConfiguration(
    val apiKey: String? = null
)

fun createCoreServiceWebClientBuilder(baseUrl: String, apiKey: String?): WebClient.Builder {
    val builder = WebClient.builder()
        .baseUrl(baseUrl)
        .exchangeStrategies(
            ExchangeStrategies.builder().codecs {
                it.defaultCodecs().maxInMemorySize(10000000)
            }.build()
        )
    if (apiKey != null) {
        builder.defaultHeader("Authorization", apiKey)
    }
    return builder
}
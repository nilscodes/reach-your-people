package io.vibrantnet.ryp.vibrant.configuration

import io.vibrantnet.ryp.vibrant.IntegrationVibrantApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    private val configuration: IntegrationVibrantApplicationConfiguration,
) {
    @Bean
    fun communityClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl(configuration.community.url)
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs {
                    it.defaultCodecs().maxInMemorySize(10000000)
                }.build()
            )
            .build()
}
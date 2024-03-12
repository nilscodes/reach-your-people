package io.vibrantnet.ryp.core.subscription.configuration

import io.vibrantnet.ryp.core.subscription.CoreSubscriptionConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    private val configuration: CoreSubscriptionConfiguration,
) {
    @Bean
    fun coreVerificationClient(): WebClient =
        WebClient.builder()
            .baseUrl(configuration.verifyServiceUrl)
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs {
                    it.defaultCodecs().maxInMemorySize(10000000)
                }.build())
            .build()

}
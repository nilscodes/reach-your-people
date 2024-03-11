package io.vibrantnet.ryp.core.publishing.configuration

import io.vibrantnet.ryp.core.publishing.CorePublishingConfiguration
import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    private val configuration: CorePublishingConfiguration,
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

    @Bean
    fun coreSubscriptionClient(): WebClient =
        WebClient.builder()
            .baseUrl(configuration.subscriptionServiceUrl)
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs {
                    it.defaultCodecs().maxInMemorySize(10000000)
                }.build())
            .build()

}
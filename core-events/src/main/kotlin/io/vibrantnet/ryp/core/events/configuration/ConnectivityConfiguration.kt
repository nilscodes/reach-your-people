package io.vibrantnet.ryp.core.events.configuration

import io.vibrantnet.ryp.core.events.CoreEventsConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ConnectivityConfiguration(
    private val configuration: CoreEventsConfiguration,
) {
//    @Bean
//    fun ipfsClient(): WebClient =
//        WebClient.builder()
//            .baseUrl(configuration.ipfslink)
//            .exchangeStrategies(
//                ExchangeStrategies.builder().codecs {
//                    it.defaultCodecs().maxInMemorySize(10000000)
//                }.build())
//            .build()

    @Bean
    fun cip100Client(): WebClient =
        WebClient.builder()
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs {
                    it.defaultCodecs().maxInMemorySize(10000000)
                }.build())
            .build()

}
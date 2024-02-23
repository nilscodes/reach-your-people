package io.vibrantnet.ryp.core.verification.configuration

import io.vibrantnet.ryp.core.verification.CoreVerificationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ConnectivityConfiguration(
    private val configuration: CoreVerificationConfiguration,
) {
    @Bean
    fun ipfsClient() =
        WebClient.builder()
            .baseUrl(configuration.ipfslink)
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs {
                    it.defaultCodecs().maxInMemorySize(10000000)
                }.build())
            .build()

}
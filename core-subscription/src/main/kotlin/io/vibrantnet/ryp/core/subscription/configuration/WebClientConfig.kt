package io.vibrantnet.ryp.core.subscription.configuration

import io.ryp.shared.createCoreServiceWebClientBuilder
import io.vibrantnet.ryp.core.subscription.CoreSubscriptionConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebClientConfig(
    private val configuration: CoreSubscriptionConfiguration,
) {
    @Bean
    fun coreVerificationClient() =
        createCoreServiceWebClientBuilder(configuration.verifyServiceUrl, configuration.security.apiKey).build()

}
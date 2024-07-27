package io.vibrantnet.ryp.core.publishing.configuration

import io.ryp.shared.createCoreServiceWebClientBuilder
import io.vibrantnet.ryp.core.publishing.CorePublishingConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebClientConfig(
    private val configuration: CorePublishingConfiguration,
) {
    @Bean
    fun coreVerificationClient() =
        createCoreServiceWebClientBuilder(configuration.verifyServiceUrl, configuration.security.apiKey).build()

    @Bean
    fun coreSubscriptionClient() =
        createCoreServiceWebClientBuilder(configuration.subscriptionServiceUrl, configuration.security.apiKey).build()

    @Bean
    fun coreRedirectClient() =
        createCoreServiceWebClientBuilder(configuration.redirectServiceUrl, configuration.security.apiKey).build()

}
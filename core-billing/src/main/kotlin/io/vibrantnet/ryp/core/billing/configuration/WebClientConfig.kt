package io.vibrantnet.ryp.core.billing.configuration

import io.ryp.shared.createCoreServiceWebClientBuilder
import io.vibrantnet.ryp.core.billing.CoreBillingConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebClientConfig(
    private val configuration: CoreBillingConfiguration,
) {
    @Bean
    fun coreVerificationClient() =
        createCoreServiceWebClientBuilder(configuration.verifyServiceUrl, configuration.security.apiKey).build()

}
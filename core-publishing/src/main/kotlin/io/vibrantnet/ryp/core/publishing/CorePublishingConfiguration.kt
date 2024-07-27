package io.vibrantnet.ryp.core.publishing

import io.ryp.shared.SecurityConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "io.vibrantnet.ryp")
data class CorePublishingConfiguration @ConstructorBinding constructor(
    val verifyServiceUrl: String,
    val subscriptionServiceUrl: String,
    val redirectServiceUrl: String,
    val baseUrl: String,
    val security: SecurityConfiguration = SecurityConfiguration(),
)


package io.vibrantnet.ryp.core.subscription

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "io.vibrantnet.ryp")
data class CoreSubscriptionConfiguration @ConstructorBinding constructor(
    val verifyServiceUrl: String,
)
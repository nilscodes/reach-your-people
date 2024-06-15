package io.vibrantnet.ryp.core.subscription

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "io.vibrantnet.ryp")
data class CoreSubscriptionConfiguration @ConstructorBinding constructor(
    val verifyServiceUrl: String,
    val points: PointsConfiguration = PointsConfiguration(),
)

data class PointsConfiguration(
    val enabled: Boolean = true,
    val referralPoints: Long = 10000,
    val signupPoints: Long = 5000,
    val rypTokenId: Int = 1,
)
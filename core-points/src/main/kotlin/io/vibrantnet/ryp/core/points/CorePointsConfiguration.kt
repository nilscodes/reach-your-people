package io.vibrantnet.ryp.core.points

import io.ryp.shared.SecurityConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "io.vibrantnet.ryp")
data class CorePointsConfiguration(
    val security: SecurityConfiguration = SecurityConfiguration(),
)
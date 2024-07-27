package io.vibrantnet.ryp.core.redirect

import io.ryp.shared.SecurityConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "io.vibrantnet.ryp")
data class CoreRedirectConfiguration @ConstructorBinding constructor(
    val baseUrl: String,
    val shortUrl: String,
    val redirect: RedirectConfiguration = RedirectConfiguration(),
    val security: SecurityConfiguration = SecurityConfiguration(),
)

data class RedirectConfiguration(
    val maxCacheAge: Int = 86400,
)


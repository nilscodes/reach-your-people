package io.vibrantnet.ryp.core.events

import io.ryp.shared.SecurityConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "io.vibrantnet.ryp")
data class CoreEventsConfiguration @ConstructorBinding constructor(
    val type: String = "cardano-db-sync",
//    val ipfslink: String,
    val blockfrost: BlockfrostConfig?,
    val security: SecurityConfiguration = SecurityConfiguration(),
)

data class BlockfrostConfig(
    val url: String,
    val apiKey: String,
)


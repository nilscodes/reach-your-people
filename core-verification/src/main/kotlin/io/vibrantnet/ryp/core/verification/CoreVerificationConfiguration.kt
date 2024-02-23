package io.vibrantnet.ryp.core.verification

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "io.vibrantnet.ryp")
data class CoreVerificationConfiguration @ConstructorBinding constructor(
    val type: String = "cardano-db-sync",
    val ipfslink: String,
    val blockfrost: BlockfrostConfig?,
)

data class BlockfrostConfig(
    val url: String,
    val apiKey: String,
)

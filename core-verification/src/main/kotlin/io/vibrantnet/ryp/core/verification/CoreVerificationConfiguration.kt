package io.vibrantnet.ryp.core.verification

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "io.vibrantnet.ryp")
data class CoreVerificationConfiguration @ConstructorBinding constructor(
    val type: String = "cardano-db-sync",
    val ipfslink: String,
    val blockfrost: BlockfrostConfig?,
    val libsodiumPath: String = "/usr/local/lib/libsodium.so",
    val cip22: Cip22Config = Cip22Config(),
)

data class BlockfrostConfig(
    val url: String,
    val apiKey: String,
)

data class Cip22Config(
    val domain: String = "ryp.io",
    val expirationMinutes: Long = 5L,
)
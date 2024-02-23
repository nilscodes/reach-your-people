package io.vibrantnet.ryp.core.verification

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "io.vibrantnet.ryp")
data class CoreVerificationConfiguration @ConstructorBinding constructor(
    val ipfslink: String
)

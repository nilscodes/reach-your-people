package io.vibrantnet.ryp.vibrant

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "io.hazelnet")
data class IntegrationVibrantApplicationConfiguration @ConstructorBinding constructor(
    val community: CommunityConfiguration,
)

data class CommunityConfiguration(
    val url: String
)
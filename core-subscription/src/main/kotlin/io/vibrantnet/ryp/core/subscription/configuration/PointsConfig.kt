package io.vibrantnet.ryp.core.subscription.configuration

import io.ryp.shared.aspect.PointsClaimAspect
import io.vibrantnet.ryp.core.subscription.CoreSubscriptionConfiguration
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    prefix = "io.vibrantnet.ryp.points",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class PointsConfig(
    private val configuration: CoreSubscriptionConfiguration,
    private val rabbitTemplate: RabbitTemplate,
) {
    @Bean
    fun pointsClaimAspect() = PointsClaimAspect(
        rabbitTemplate,
        "pointclaims",
        configuration.points.rypTokenId,
        mapOf(
            "referral" to configuration.points.referralPoints,
            "signup" to configuration.points.signupPoints
        ),
    )
}
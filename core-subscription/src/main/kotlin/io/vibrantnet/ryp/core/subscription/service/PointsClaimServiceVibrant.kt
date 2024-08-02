package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.model.points.PointsClaimDto
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class PointsClaimServiceVibrant(
    private val rabbitTemplate: RabbitTemplate,
) : PointsClaimService {
    override fun sendPointsClaim(pointsClaim: PointsClaimDto) {
        rabbitTemplate.convertAndSend("pointclaims", pointsClaim)
    }
}
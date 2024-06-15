package io.vibrantnet.ryp.core.points.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ryp.shared.model.points.PointsClaimDto
import io.vibrantnet.ryp.core.points.model.DuplicatePointsClaimException
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

val logger = KotlinLogging.logger {}

@Service
class PointsQueueService(
    private val pointsApiService: PointsApiService,
    private val rabbitTemplate: RabbitTemplate,
) {

    @RabbitListener(queues = ["pointclaims"])
    fun savePointsClaimFromQueue(pointsClaimDto: PointsClaimDto) {
        logger.debug { "Received point claim from message queue: $pointsClaimDto" }
        pointsApiService.createPointClaim(
            pointsClaimDto.accountId,
            pointsClaimDto.tokenId,
            pointsClaimDto.claimId,
            pointsClaimDto
        ).onErrorResume(DataIntegrityViolationException::class.java) { e: Throwable ->
            logger.error(e) { "Failed to create point claim from message queue. Likely cause is a token with ID ${pointsClaimDto.tokenId} that does not exist" }
            Mono.empty()
        }.onErrorResume(DuplicatePointsClaimException::class.java) {
            logger.info { "Point claim with ID ${pointsClaimDto.claimId} already exists. Ignored without errors." }
            Mono.empty()
        }.map {
            rabbitTemplate.convertAndSend("pointclaimsfulfilled", it)
        }.subscribe()
    }
}
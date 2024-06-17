package io.vibrantnet.ryp.core.verification.service

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.ryp.shared.model.SnapshotRequestDto
import io.vibrantnet.ryp.core.verification.persistence.TokenDao
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.UUID

val logger = KotlinLogging.logger {}

@Service
class SnapshotServiceVibrant(
    val tokenDao: TokenDao,
    val redisTemplate: RedisTemplate<String, Any>,
    val rabbitTemplate: RabbitTemplate
) {

    @RabbitListener(queues = ["snapshot"])
    fun processSnapshot(snapshotRequest: SnapshotRequestDto) {
        logger.info { "Processing snapshot request for ${snapshotRequest.announcementRequest.announcementId} with policy ids ${snapshotRequest.policyIds}" }
        val snapshot = tokenDao.getMultiAssetCountSnapshotForPolicyId(snapshotRequest.policyIds.map { PolicyId(it) })
        val snapshotUuid = UUID.randomUUID()
        if (snapshot.isNotEmpty()) {
            logger.info { "Snapshot for ${snapshotRequest.announcementRequest.announcementId} completed with ${snapshot.size} assets" }
            redisTemplate.opsForList().rightPushAll("snapshot:$snapshotUuid", snapshot)
            redisTemplate.expire("snapshot:$snapshotUuid", 48, java.util.concurrent.TimeUnit.HOURS)
        } else {
            logger.warn { "Snapshot for ${snapshotRequest.announcementRequest.announcementId} completed with no assets" }
        }
        rabbitTemplate.convertAndSend("snapshotcompleted", snapshotRequest.announcementRequest.copy(snapshotId = snapshotUuid))
    }
}
package io.vibrantnet.ryp.core.points.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.ryp.shared.model.points.PointsClaimDto
import io.vibrantnet.ryp.core.points.model.DuplicatePointsClaimException
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

class PointsQueueServiceTest {

    @Test
    fun `sending a point claim to the queue processes correctly`() {
        val pointsApiService = mockk<PointsApiService>()
        val rabbitTemplate = mockk<RabbitTemplate>()
        val pointsQueueService = PointsQueueService(pointsApiService, rabbitTemplate)
        val pointClaim = makePointClaim()
        every { pointsApiService.createPointClaim(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId, pointClaim) } answers {
            Mono.just(pointClaim)
        }
        pointsQueueService.savePointsClaimFromQueue(pointClaim)
        verify(exactly = 1) {
            rabbitTemplate.convertAndSend("pointclaimsfulfilled", pointClaim)
        }
    }

    @Test
    fun `sending an already existing point claim to the queue is a no-operation but does not crash the queue processing`() {
        val pointsApiService = mockk<PointsApiService>()
        val rabbitTemplate = mockk<RabbitTemplate>()
        val pointsQueueService = PointsQueueService(pointsApiService, rabbitTemplate)
        val pointClaim = makePointClaim()
        every { pointsApiService.createPointClaim(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId, pointClaim) } answers {
            Mono.error(DuplicatePointsClaimException("Claim with ID ${pointClaim.claimId} already exists"))
        }

        pointsQueueService.savePointsClaimFromQueue(pointClaim)
        verify(exactly = 0) {
            rabbitTemplate.convertAndSend("pointclaimsfulfilled", any<PointsClaimDto>())
        }
    }

    @Test
    fun `sending a point claim to the queue that has no matching token is a no-operation but does not crash the queue processing`() {
        val pointsApiService = mockk<PointsApiService>()
        val rabbitTemplate = mockk<RabbitTemplate>()
        val pointsQueueService = PointsQueueService(pointsApiService, rabbitTemplate)
        val pointClaim = makePointClaim()
        every { pointsApiService.createPointClaim(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId, pointClaim) } answers {
            Mono.error(DataIntegrityViolationException("Dunno man, this one just doesn't exist"))
        }

        pointsQueueService.savePointsClaimFromQueue(pointClaim)
        verify(exactly = 0) {
            rabbitTemplate.convertAndSend("pointclaimsfulfilled", any<PointsClaimDto>())
        }
    }

    private fun makePointClaim() = PointsClaimDto(
        claimId = "signup-12",
        category = "signup",
        tokenId = 18,
        accountId = 12,
        points = 100,
        projectId = 119,
        claimed = false,
        claimTime = null,
        createTime = OffsetDateTime.parse("2021-01-01T00:00:00Z"),
        expirationTime = OffsetDateTime.parse("2025-02-01T00:00:00Z"),
    )
}
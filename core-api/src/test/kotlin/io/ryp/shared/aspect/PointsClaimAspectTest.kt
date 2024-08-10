package io.ryp.shared.aspect

import io.mockk.*
import io.ryp.shared.model.points.PointsClaimDto
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import reactor.core.publisher.Mono
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PointsClaimAspectTest {

    private lateinit var rabbitTemplate: RabbitTemplate
    private lateinit var aspect: PointsClaimAspect

    private val queueName = "testQueue"
    private val rypTokenId = 123
    private val pointsMap = mapOf("reward" to 100L)

    @BeforeEach
    fun setup() {
        rabbitTemplate = mockk<RabbitTemplate>(relaxed = true)
        aspect = PointsClaimAspect(rabbitTemplate, queueName, rypTokenId, pointsMap)
    }

    @Test
    fun `should process points claim and send to RabbitMQ for reactive service method`() {
        val joinPoint = mockk<JoinPoint>(relaxed = true)
        val methodSignature = mockk<MethodSignature>()
        val method = TestService::class.java.getMethod("claimPointsReactive", Long::class.java)
        every { joinPoint.signature } returns methodSignature
        every { methodSignature.method } returns method
        every { joinPoint.args } returns arrayOf(12L)

        val pointsClaimSlot = slot<PointsClaimDto>()

        aspect.afterReturningAdvice(joinPoint, Mono.just("result"))

        verify { rabbitTemplate.convertAndSend(eq(queueName), capture(pointsClaimSlot)) }
        val capturedPointsClaim = pointsClaimSlot.captured

        assertEquals(100L, capturedPointsClaim.points)
        assertEquals("categoryValue", capturedPointsClaim.category)
        assertEquals("claimIdValue", capturedPointsClaim.claimId)
        assertEquals(12L, capturedPointsClaim.accountId)
        assertEquals(rypTokenId, capturedPointsClaim.tokenId)
        assertTrue(capturedPointsClaim.claimed)
    }

    @Test
    fun `should process points claim and send to RabbitMQ for non-reactive service method`() {
        val joinPoint = mockk<JoinPoint>(relaxed = true)
        val methodSignature = mockk<MethodSignature>()
        val method = TestService::class.java.getMethod("claimPointsNonReactive", Long::class.java)
        every { joinPoint.signature } returns methodSignature
        every { methodSignature.method } returns method
        every { joinPoint.args } returns arrayOf(12L)

        val pointsClaimSlot = slot<PointsClaimDto>()

        aspect.afterReturningAdvice(joinPoint, "result")

        verify { rabbitTemplate.convertAndSend(eq(queueName), capture(pointsClaimSlot)) }
        val capturedPointsClaim = pointsClaimSlot.captured

        assertEquals(100L, capturedPointsClaim.points)
        assertEquals("categoryValue", capturedPointsClaim.category)
        assertEquals("claimIdValue", capturedPointsClaim.claimId)
        assertEquals(12L, capturedPointsClaim.accountId)
        assertEquals(rypTokenId, capturedPointsClaim.tokenId)
        assertTrue(capturedPointsClaim.claimed)
    }

    @Test
    fun `should process points claim and send to RabbitMQ for reactive service method with multiple claims`() {
        val joinPoint = mockk<JoinPoint>(relaxed = true)
        val methodSignature = mockk<MethodSignature>()
        val method = TestService::class.java.getMethod("claimPointsReactiveWithMultipleClaims", Long::class.java)
        every { joinPoint.signature } returns methodSignature
        every { methodSignature.method } returns method
        every { joinPoint.args } returns arrayOf(12L)

        val pointsClaimSlot = mutableListOf<PointsClaimDto>()

        aspect.afterReturningAdvice(joinPoint, Mono.just("result"))

        verify(exactly = 2) { rabbitTemplate.convertAndSend(eq(queueName), capture(pointsClaimSlot)) }
        val capturedPointsClaim = pointsClaimSlot.first()

        assertEquals(100L, capturedPointsClaim.points)
        assertEquals("categoryValue", capturedPointsClaim.category)
        assertEquals("claimIdValue", capturedPointsClaim.claimId)
        assertEquals(12L, capturedPointsClaim.accountId)
        assertEquals(rypTokenId, capturedPointsClaim.tokenId)
        assertTrue(capturedPointsClaim.claimed)

        val capturedPointsClaim2 = pointsClaimSlot.last()
        assertEquals(200L, capturedPointsClaim2.points)
        assertEquals("categoryValue2", capturedPointsClaim2.category)
        assertEquals("claimIdValue2", capturedPointsClaim2.claimId)
        assertEquals(2L, capturedPointsClaim2.accountId)
        assertEquals(rypTokenId, capturedPointsClaim2.tokenId)
        assertTrue(capturedPointsClaim2.claimed)
    }

    @Test
    fun `should not process points claim and send to RabbitMQ for reactive service method with invalid expression`() {
        val joinPoint = mockk<JoinPoint>(relaxed = true)
        val methodSignature = mockk<MethodSignature>()
        val method = TestService::class.java.getMethod("claimPointsReactiveWithInvalidExpression", Long::class.java)
        every { joinPoint.signature } returns methodSignature
        every { methodSignature.method } returns method
        every { joinPoint.args } returns arrayOf(12L)

        aspect.afterReturningAdvice(joinPoint, Mono.just("result"))

        verify { rabbitTemplate wasNot Called }
    }

    // A dummy service class for testing purposes
    class TestService {
        @PointsClaim(
            points = "#points['reward']",
            category = "'categoryValue'",
            claimId = "'claimIdValue'",
            accountId = "#accountId",
            tokenId = "#rypTokenId",
            claimed = "true"
        )
        fun claimPointsReactive(accountId: Long): Mono<String> {
            return Mono.just("result")
        }

        @PointsClaim(
            points = "#points['reward']",
            category = "'categoryValue'",
            claimId = "'claimIdValue'",
            accountId = "#accountId",
            tokenId = "#rypTokenId",
            claimed = "true"
        )
        fun claimPointsNonReactive(accountId: Long): String {
            return "result"
        }

        @PointsClaim(
            points = "#points['reward']",
            category = "'categoryValue'",
            claimId = "'claimIdValue'",
            accountId = "#accountId",
            tokenId = "#rypTokenId",
            claimed = "true"
        )
        @PointsClaim(
            points = "#points['reward'] * 2",
            category = "'categoryValue2'",
            claimId = "'claimIdValue2'",
            accountId = "2",
            tokenId = "#rypTokenId",
            claimed = "true"
        )
        fun claimPointsReactiveWithMultipleClaims(accountId: Long): Mono<String> {
            return Mono.just("result")
        }

        @PointsClaim(
            points = "#points['reward']x",
            category = "'categoryValue'",
            claimId = "'claimIdValue'",
            accountId = "#accountId",
            tokenId = "#rypTokenId",
            claimed = "true"
        )
        fun claimPointsReactiveWithInvalidExpression(accountId: Long): Mono<String> {
            return Mono.just("result")
        }
    }
}

package io.vibrantnet.ryp.core.points.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.ryp.shared.model.points.PointsClaimDto
import io.ryp.shared.model.points.PointsClaimPartialDto
import io.vibrantnet.ryp.core.points.model.DuplicatePointsClaimException
import io.vibrantnet.ryp.core.points.model.PointsSummaryDto
import io.vibrantnet.ryp.core.points.persistence.PointsByTokenProjection
import io.vibrantnet.ryp.core.points.persistence.PointsClaim
import io.vibrantnet.ryp.core.points.persistence.PointsClaimRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.util.*

class PointsApiServiceVibrantTest {

    private val pointsClaimRepository = mockk<PointsClaimRepository>()
    private val transactionTemplate = mockk<TransactionTemplate>()
    private val service = PointsApiServiceVibrant(pointsClaimRepository, transactionTemplate)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        every { transactionTemplate.execute(any<TransactionCallback<*>>()) } answers {
            firstArg<TransactionCallback<*>>().doInTransaction(mockk<TransactionStatus>())
        }
    }

    @Test
    fun `creating a point claim works if no claim with the same ID exists, and create time cannot be externally provided`() {
        val pointClaim = makePointClaimDto("signup-12").copy(
            createTime = OffsetDateTime.now()
        )

        every { pointsClaimRepository.existsById(pointClaim.claimId) } returns false
        every { pointsClaimRepository.save(any()) } returns makePointClaim("signup-12")

        val createdPointClaim =
            service.createPointClaim(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId, pointClaim)

        StepVerifier.create(createdPointClaim)
            .expectNext(makePointClaimDto("signup-12"))
            .verifyComplete()
    }

    @Test
    fun `creating a point claim fails if a claim with the same ID exists`() {
        val pointClaim = makePointClaimDto("signup-12")

        every { pointsClaimRepository.existsById(pointClaim.claimId) } returns true

        val createdPointClaim =
            service.createPointClaim(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId, pointClaim)

        StepVerifier.create(createdPointClaim)
            .expectError(DuplicatePointsClaimException::class.java)
            .verify()
    }

    @Test
    fun `claim time is automatically set if claimed points are submitted`() {
        val pointClaim = makePointClaimDto("signup-12").copy(
            claimed = true
        )

        every { pointsClaimRepository.existsById(pointClaim.claimId) } returns false
        every { pointsClaimRepository.save(any()) } returnsArgument 0

        val createdPointClaim =
            service.createPointClaim(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId, pointClaim)

        StepVerifier.create(createdPointClaim)
            .assertNext() {
                assert(it.claimTime != null)
            }
            .verifyComplete()
    }

    @Test
    fun `getting point claims for an account works`() {
        val pointClaims = listOf(
            makePointClaim("signup-12"),
            makePointClaim("referral-13"),
        )

        every { pointsClaimRepository.findAllByAccountId(12) } returns pointClaims

        val retrievedPointClaims = service.getPointClaimsForAccount(12)

        StepVerifier.create(retrievedPointClaims)
            .expectNext(makePointClaimDto("signup-12"))
            .expectNext(makePointClaimDto("referral-13"))
            .verifyComplete()
    }

    @Test
    fun `getting point claims for an account and a specific token works`() {
        val pointClaim1 = makePointClaim("signup-12")
        val pointClaims = listOf(
            pointClaim1,
            makePointClaim("referral-13"),
        )

        every { pointsClaimRepository.findAllByAccountIdAndTokenId(pointClaim1.accountId, pointClaim1.tokenId) } returns pointClaims

        val retrievedPointClaims = service.getPointClaimsForAccountAndToken(pointClaim1.accountId, pointClaim1.tokenId)

        StepVerifier.create(retrievedPointClaims)
            .expectNext(makePointClaimDto("signup-12"))
            .expectNext(makePointClaimDto("referral-13"))
            .verifyComplete()
    }

    @Test
    fun `getting points summary for an account works`() {
        val totalPointsClaimed = listOf(
            makePointsByTokenProjection(12, 100),
            makePointsByTokenProjection(13, 100)
        )
        val totalPointsAvailable = listOf(
            makePointsByTokenProjection(12, 100),
            makePointsByTokenProjection(13, 0),
            makePointsByTokenProjection(14, 1000)
        )
        val totalPointsSpent = listOf(
            makePointsByTokenProjection(12, 0),
            makePointsByTokenProjection(13, 100)
        )
        val totalPointsClaimable = listOf(
            makePointsByTokenProjection(14, 600)
        )

        every { pointsClaimRepository.getTotalPointsClaimedByTokenIdAndAccountId(12) } returns totalPointsClaimed
        every { pointsClaimRepository.getTotalPointsAvailableByTokenIdAndAccountId(12) } returns totalPointsAvailable
        every { pointsClaimRepository.getTotalPointsSpentByTokenIdAndAccountId(12) } returns totalPointsSpent
        every { pointsClaimRepository.getTotalPointsClaimableByTokenIdAndAccountId(12) } returns totalPointsClaimable

        val pointsSummary = service.getPointsSummaryForAccount(12)

        StepVerifier.create(pointsSummary.collectList())
            .expectNext(
                listOf(
                    PointsSummaryDto(
                        tokenId = 12,
                        totalPointsClaimed = 100,
                        totalPointsAvailable = 100,
                        totalPointsSpent = 0,
                        totalPointsClaimable = 0
                    ),
                    PointsSummaryDto(
                        tokenId = 13,
                        totalPointsClaimed = 100,
                        totalPointsAvailable = 0,
                        totalPointsSpent = 100,
                        totalPointsClaimable = 0
                    ),
                    PointsSummaryDto(
                        tokenId = 14,
                        totalPointsClaimed = 0,
                        totalPointsAvailable = 1000,
                        totalPointsSpent = 0,
                        totalPointsClaimable = 600
                    )
                )
            )
            .verifyComplete()
    }

    @Test
    fun `getting a specific claim id for an account and token works`() {
        val pointClaim = makePointClaim("signup-12")

        every { pointsClaimRepository.findById(pointClaim.claimId) } returns Optional.of(pointClaim)

        val actual =
            service.getSpecificPointClaimForAccountAndToken(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId)

        StepVerifier.create(actual)
            .expectNext(makePointClaimDto(pointClaim.claimId))
            .verifyComplete()
    }

    @Test
    fun `getting a specific claim id for an account and token fails if the claim does not exist `() {
        every { pointsClaimRepository.findById("signup-12") } returns Optional.empty()

        val retrievedPointClaim = service.getSpecificPointClaimForAccountAndToken(12, 1, "signup-12")

        StepVerifier.create(retrievedPointClaim)
            .expectError(NoSuchElementException::class.java)
            .verify()
    }

    @Test
    fun `getting a specific claim id for an account and token fails if the claim does not belong to the account`() {
        val pointClaim = makePointClaim("signup-12")

        every { pointsClaimRepository.findById("signup-12") } returns Optional.of(pointClaim)

        val retrievedPointClaim = service.getSpecificPointClaimForAccountAndToken(13, 1, "signup-12")

        StepVerifier.create(retrievedPointClaim)
            .expectError(NoSuchElementException::class.java)
            .verify()
    }

    @Test
    fun `getting a specific claim id for an account and token fails if the claim does not belong to the token`() {
        val pointClaim = makePointClaim("signup-12")

        every { pointsClaimRepository.findById("signup-12") } returns Optional.of(pointClaim)

        val retrievedPointClaim = service.getSpecificPointClaimForAccountAndToken(12, 2, "signup-12")

        StepVerifier.create(retrievedPointClaim)
            .expectError(NoSuchElementException::class.java)
            .verify()
    }

    @Test
    fun `updating a point claim status works and sets the claim time`() {
        val pointClaim = makePointClaim("signup-12")

        every { pointsClaimRepository.findById(pointClaim.claimId) } returns Optional.of(pointClaim)
        every { pointsClaimRepository.save(pointClaim) } returnsArgument 0

        val update = PointsClaimPartialDto(
            claimed = true
        )
        val updatedPointClaim = service.updatePointClaim(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId, update)

        StepVerifier.create(updatedPointClaim)
            .assertNext {
                assertTrue(it.claimed)
                assertNotNull(it.claimTime)
            }
            .verifyComplete()
    }

    @Test
    fun `updating a point claim expiration time works`() {
        val pointClaim = makePointClaim("signup-12")

        every { pointsClaimRepository.findById(pointClaim.claimId) } returns Optional.of(pointClaim)
        every { pointsClaimRepository.save(pointClaim) } returnsArgument 0

        val update = PointsClaimPartialDto(
            expirationTime = OffsetDateTime.parse("2025-03-01T00:00:00Z")
        )
        val updatedPointClaim = service.updatePointClaim(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId, update)

        StepVerifier.create(updatedPointClaim)
            .assertNext {
                assertEquals(OffsetDateTime.parse("2025-03-01T00:00:00Z"), it.expirationTime)
            }
            .verifyComplete()
    }

    @Test
    fun `updating claim status and expiration time at the same time only sets the claim status`() {
        val pointClaim = makePointClaim("signup-12")

        every { pointsClaimRepository.findById(pointClaim.claimId) } returns Optional.of(pointClaim)
        every { pointsClaimRepository.save(pointClaim) } returnsArgument 0

        val update = PointsClaimPartialDto(
            claimed = true,
            expirationTime = OffsetDateTime.parse("2025-03-01T00:00:00Z")
        )
        val updatedPointClaim = service.updatePointClaim(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId, update)

        StepVerifier.create(updatedPointClaim)
            .assertNext {
                assertTrue(it.claimed)
                assertNotNull(it.claimTime)
                assertEquals(pointClaim.expirationTime, it.expirationTime)
            }
            .verifyComplete()
    }

    @Test
    fun `claiming an already claimed point claim fails`() {
        val pointClaim = makePointClaim("signup-12", true)

        every { pointsClaimRepository.findById(pointClaim.claimId) } returns Optional.of(pointClaim)

        val update = PointsClaimPartialDto(
            claimed = true
        )
        val updatedPointClaim = service.updatePointClaim(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId, update)

        StepVerifier.create(updatedPointClaim)
            .expectError(IllegalStateException::class.java)
            .verify()
    }

    @Test
    fun `updating a points claim not associated with the right account fails`() {
        val pointClaim = makePointClaim("signup-12")

        every { pointsClaimRepository.findById(pointClaim.claimId) } returns Optional.of(pointClaim)

        val update = PointsClaimPartialDto(
            claimed = true
        )
        val updatedPointClaim = service.updatePointClaim(13, pointClaim.tokenId, pointClaim.claimId, update)

        StepVerifier.create(updatedPointClaim)
            .expectError(NoSuchElementException::class.java)
            .verify()
    }

    @Test
    fun `updating a points claim not associated with the right token fails`() {
        val pointClaim = makePointClaim("signup-12")

        every { pointsClaimRepository.findById(pointClaim.claimId) } returns Optional.of(pointClaim)

        val update = PointsClaimPartialDto(
            claimed = true
        )
        val updatedPointClaim = service.updatePointClaim(pointClaim.accountId, 2, pointClaim.claimId, update)

        StepVerifier.create(updatedPointClaim)
            .expectError(NoSuchElementException::class.java)
            .verify()
    }

    @Test
    fun `claiming an already expired point claim fails`() {
        val pointClaim = makePointClaim("signup-12")
        pointClaim.expirationTime = OffsetDateTime.parse("2020-02-01T00:00:00Z")

        every { pointsClaimRepository.findById(pointClaim.claimId) } returns Optional.of(pointClaim)

        val update = PointsClaimPartialDto(
            claimed = true
        )
        val updatedPointClaim = service.updatePointClaim(pointClaim.accountId, pointClaim.tokenId, pointClaim.claimId, update)

        StepVerifier.create(updatedPointClaim)
            .expectError(IllegalStateException::class.java)
            .verify()
    }

    private fun makePointClaim(claimId: String, claimed: Boolean = false) = PointsClaim(
        claimId = claimId,
        category = "signup",
        tokenId = 18,
        accountId = 12,
        points = 100,
        projectId = 119,
        claimed = claimed,
        claimTime = null,
        createTime = OffsetDateTime.parse("2021-01-01T00:00:00Z"),
        expirationTime = null,
    )

    private fun makePointClaimDto(claimId: String) = PointsClaimDto(
        claimId = claimId,
        category = "signup",
        tokenId = 18,
        accountId = 12,
        points = 100,
        projectId = 119,
        claimed = false,
        claimTime = null,
        createTime = OffsetDateTime.parse("2021-01-01T00:00:00Z"),
        expirationTime = null,
    )

    fun makePointsByTokenProjection(tokenId: Long, points: Long): PointsByTokenProjection {
        return object : PointsByTokenProjection {
            override val tokenId: Long = tokenId
            override val points: Long = points
        }
    }
}
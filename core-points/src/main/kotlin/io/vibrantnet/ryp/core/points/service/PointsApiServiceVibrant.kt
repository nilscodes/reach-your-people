package io.vibrantnet.ryp.core.points.service

import io.ryp.shared.model.points.PointsClaimDto
import io.ryp.shared.model.points.PointsClaimPartialDto
import io.vibrantnet.ryp.core.points.model.DuplicatePointsClaimException
import io.vibrantnet.ryp.core.points.model.PointsSummaryDto
import io.vibrantnet.ryp.core.points.persistence.PointsClaim
import io.vibrantnet.ryp.core.points.persistence.PointsClaimRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@Service
class PointsApiServiceVibrant(
    val pointsClaimRepository: PointsClaimRepository,
    val transactionTemplate: TransactionTemplate,
) : PointsApiService {

    /*
     * Currently this call is not using @Transactional because the blocking nature interferes with the queue processing service
     */
    override fun createPointClaim(
        accountId: Long,
        tokenId: Int,
        claimId: String,
        pointsClaimDto: PointsClaimDto
    ): Mono<PointsClaimDto> {
        return Mono.fromCallable {
            transactionTemplate.execute {
                if (pointsClaimRepository.existsById(claimId)) {
                    throw DuplicatePointsClaimException("Claim with ID $claimId already exists")
                }
                val pointsClaim = PointsClaim(
                    claimId = claimId,
                    points = pointsClaimDto.points,
                    category = pointsClaimDto.category,
                    accountId = accountId,
                    tokenId = tokenId,
                    claimed = pointsClaimDto.claimed,
                    projectId = pointsClaimDto.projectId,
                    expirationTime = pointsClaimDto.expirationTime,
                    claimTime = if (pointsClaimDto.claimed) OffsetDateTime.now() else null,
                )
                pointsClaimRepository.save(pointsClaim).toDto()
            }
        }
    }

    override fun getPointClaimsForAccount(accountId: Long): Flux<PointsClaimDto> {
        return Flux.fromIterable(pointsClaimRepository.findAllByAccountId(accountId).map { it.toDto() })
    }

    override fun getPointClaimsForAccountAndToken(accountId: Long, tokenId: Int): Flux<PointsClaimDto> {
        return Flux.fromIterable(
            pointsClaimRepository.findAllByAccountIdAndTokenId(accountId, tokenId).map { it.toDto() })
    }

    override fun getPointsSummaryForAccount(accountId: Long): Flux<PointsSummaryDto> {
        val totalPointsClaimed = pointsClaimRepository.getTotalPointsClaimedByTokenIdAndAccountId(accountId)
            .associate { it.tokenId to it.points }

        val totalPointsSpent = pointsClaimRepository.getTotalPointsSpentByTokenIdAndAccountId(accountId)
            .associate { it.tokenId to it.points }

        val totalPointsClaimable = pointsClaimRepository.getTotalPointsClaimableByTokenIdAndAccountId(accountId)
            .associate { it.tokenId to it.points }

        val totalPointsAvailable = pointsClaimRepository.getTotalPointsAvailableByTokenIdAndAccountId(accountId)
            .associate { it.tokenId to it.points }

        val allTokenIds =
            (totalPointsClaimed.keys + totalPointsSpent.keys + totalPointsClaimable.keys + totalPointsAvailable.keys).toSet()

        val pointsSummaries = allTokenIds.map { tokenId ->
            PointsSummaryDto(
                tokenId = tokenId,
                totalPointsClaimed = totalPointsClaimed[tokenId] ?: 0,
                totalPointsAvailable = totalPointsAvailable[tokenId] ?: 0,
                totalPointsSpent = totalPointsSpent[tokenId] ?: 0,
                totalPointsClaimable = totalPointsClaimable[tokenId] ?: 0
            )
        }

        return Flux.fromIterable(pointsSummaries)
    }

    override fun getSpecificPointClaimForAccountAndToken(
        accountId: Long,
        tokenId: Int,
        claimId: String
    ): Mono<PointsClaimDto> {
        val pointsClaimOptional = pointsClaimRepository.findById(claimId)
        if (pointsClaimOptional.isPresent) {
            val pointsClaim = pointsClaimOptional.get()
            if (pointsClaim.accountId == accountId && pointsClaim.tokenId == tokenId) {
                return Mono.just(pointsClaim.toDto())
            }
        }
        return Mono.error(NoSuchElementException("Claim with ID $claimId does not exist for account $accountId and token $tokenId"))
    }

    override fun updatePointClaim(
        accountId: Long,
        tokenId: Int,
        claimId: String,
        pointsClaimPartialDto: PointsClaimPartialDto
    ): Mono<PointsClaimDto> {
        return Mono.justOrEmpty(pointsClaimRepository.findById(claimId))
            .switchIfEmpty(Mono.error(NoSuchElementException("Claim with ID $claimId does not exist")))
            .flatMap { pointsClaim ->
                when {
                    pointsClaim.accountId != accountId || pointsClaim.tokenId != tokenId -> {
                        // Claim exists, but not for the given account and token
                        Mono.error(NoSuchElementException("Claim with ID $claimId does not exist for account $accountId and token $tokenId"))
                    }
                    pointsClaim.claimed -> {
                        Mono.error(IllegalStateException("Claim with ID $claimId has already been claimed and cannot be modified"))
                    }
                    pointsClaimPartialDto.claimed == true && pointsClaim.hasExpired() -> {
                        Mono.error(IllegalStateException("Claim with ID $claimId expired on ${pointsClaim.expirationTime} and cannot be claimed"))
                    }
                    else -> {
                        if (pointsClaimPartialDto.claimed == true) {
                            pointsClaim.claimTime = OffsetDateTime.now()
                            pointsClaim.claimed = true
                        } else {
                            // Can only change the expiration time if not claiming at the same time (otherwise might be able to have a claim that is already expired)
                            pointsClaim.expirationTime = pointsClaimPartialDto.expirationTime ?: pointsClaim.expirationTime
                        }
                        Mono.just(pointsClaimRepository.save(pointsClaim).toDto())
                    }
                }
            }
    }
}
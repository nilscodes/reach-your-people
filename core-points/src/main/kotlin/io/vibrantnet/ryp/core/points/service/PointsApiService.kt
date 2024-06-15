package io.vibrantnet.ryp.core.points.service

import io.ryp.shared.model.points.PointsClaimDto
import io.ryp.shared.model.points.PointsClaimPartialDto
import io.vibrantnet.ryp.core.points.model.PointsSummaryDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PointsApiService {

    /**
     * POST /points/accounts/{accountId}/claims/{tokenId}/{claimId} : Create points claim
     * Create a specific points claim for the
     *
     * @param accountId The numeric ID of an account (required)
     * @param tokenId The numeric ID of the token (required)
     * @param claimId The unique ID of the claim (required)
     * @param pointsClaimDto  (optional)
     * @return OK (status code 200)
     *         or A claim already exists for this ID (status code 409)
     * @see PointsApi#createPointClaim
     */
    fun createPointClaim(accountId: Long, tokenId: Int, claimId: String, pointsClaimDto: PointsClaimDto): Mono<PointsClaimDto>

    /**
     * GET /points/accounts/{accountId}/claims : Get point claims for user
     * Get all point claims for this user, i.e. all individual points they are either able to claim or have claimed, regardless of the token.
     *
     * @param accountId The numeric ID of an account (required)
     * @return OK (status code 200)
     * @see PointsApi#getPointClaimsForAccount
     */
    fun getPointClaimsForAccount(accountId: Long): Flux<PointsClaimDto>

    /**
     * GET /points/accounts/{accountId}/claims/{tokenId} : Get point claims for user
     * Get all point claims for this user, i.e. all individual points they are either able to claim or have claimed, regardless of the token.
     *
     * @param accountId The numeric ID of an account (required)
     * @param tokenId The numeric ID of the token (required)
     * @return OK (status code 200)
     * @see PointsApi#getPointClaimsForAccountAndToken
     */
    fun getPointClaimsForAccountAndToken(accountId: Long, tokenId: Int): Flux<PointsClaimDto>

    /**
     * GET /points/accounts/{accountId} : Get points summary for account
     * Get a summary of points for each token for this user, including total accrued, spent and available
     *
     * @param accountId The numeric ID of an account (required)
     * @return OK (status code 200)
     * @see PointsApi#getPointsSummaryForAccount
     */
    fun getPointsSummaryForAccount(accountId: Long): Flux<PointsSummaryDto>

    /**
     * GET /points/accounts/{accountId}/claims/{tokenId}/{claimId} : Get specific points claim
     * Find out if a specific claim for a given token and claim ID exists for this account.
     *
     * @param accountId The numeric ID of an account (required)
     * @param tokenId The numeric ID of the token (required)
     * @param claimId The unique ID of the claim (required)
     * @return OK (status code 200)
     *         or No claim with this ID found for the account and token that was requested (status code 404)
     * @see PointsApi#getSpecificPointClaimForAccountAndToken
     */
    fun getSpecificPointClaimForAccountAndToken(accountId: Long, tokenId: Int, claimId: String): Mono<PointsClaimDto>

    /**
     * PATCH /points/accounts/{accountId}/claims/{tokenId}/{claimId} : Update points claim
     * Claim a specific point claim for this account and token
     *
     * @param accountId The numeric ID of an account (required)
     * @param tokenId The numeric ID of the token (required)
     * @param claimId The unique ID of the claim (required)
     * @param pointsClaimPartialDto  (optional)
     * @return Points claim updated (status code 200)
     *         or No claim with this ID found for the account and token that was requested (status code 404)
     * @see PointsApi#updatePointClaim
     */
    fun updatePointClaim(accountId: Long, tokenId: Int, claimId: String, pointsClaimPartialDto: PointsClaimPartialDto): Mono<PointsClaimDto>
}

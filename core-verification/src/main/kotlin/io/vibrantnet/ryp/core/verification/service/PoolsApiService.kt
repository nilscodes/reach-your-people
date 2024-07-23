package io.vibrantnet.ryp.core.verification.service

import io.ryp.cardano.model.StakepoolDetailsDto
import io.ryp.cardano.model.StakepoolVerificationDto
import reactor.core.publisher.Mono

interface PoolsApiService {
    /**
     * GET /pools/{poolHash} : Get stakepool details
     *
     * @param poolHash The hash of a Cardano stakepool (required)
     * @return The stakepool details (status code 200)
     * @see PoolsApi#getStakepoolDetails
     */
    fun getStakepoolDetails(poolHash: String): Mono<StakepoolDetailsDto>

    /**
     * POST /pools/{poolHash}/verifications : Create a new verification flow for stake pool
     *
     * @param poolHash The hash of a Cardano stakepool (required)
     * @return The stakepool verification details to process (status code 201)
     * @see PoolsApi#startStakepoolVerification
     */
    fun startStakepoolVerification(poolHash: String): Mono<StakepoolVerificationDto>

    /**
     * POST /pools/{poolHash}/verifications/{verificationNonce} : Test verification flow for stake pool
     *
     * @param poolHash The hash of a Cardano stakepool (required)
     * @param verificationNonce The nonce to use in the verification flow (required)
     * @param stakepoolVerification  (optional)
     * @return Confirmed stakepool verification, verification not persisted (status code 200)
     *         or Stakepool verification not found under this nonce and likely expired (status code 404)
     *         or Stakepool verification denied because of invalid signature or similar discrepancy (status code 409)
     * @see PoolsApi#testStakepoolVerification
     */
    fun testStakepoolVerification(poolHash: String, verificationNonce: String, stakepoolVerification: StakepoolVerificationDto): Mono<StakepoolVerificationDto>

    /**
     * PUT /pools/{poolHash}/verifications/{verificationNonce} : Complete verification flow for stake pool
     *
     * @param poolHash The hash of a Cardano stakepool (required)
     * @param verificationNonce The nonce to use in the verification flow (required)
     * @param stakepoolVerification  (optional)
     * @return Confirmed stakepool verification, and verification persisted (status code 200)
     *         or Stakepool verification not found under this nonce and likely expired (status code 404)
     *         or Stakepool verification denied because of invalid signature or similar discrepancy (status code 409)
     * @see PoolsApi#completeStakepoolVerification
     */
    fun completeStakepoolVerification(poolHash: String, verificationNonce: String, stakepoolVerification: StakepoolVerificationDto): Mono<StakepoolVerificationDto>

}

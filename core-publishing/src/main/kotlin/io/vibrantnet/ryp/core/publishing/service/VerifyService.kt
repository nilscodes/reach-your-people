package io.vibrantnet.ryp.core.publishing.service

import io.ryp.cardano.model.stakepools.StakepoolDetailsDto
import reactor.core.publisher.Mono

interface VerifyService {
    fun verifyCip66(
        policyId: String,
        serviceName: String,
        referenceId: String,
    ): Mono<Boolean>

    fun getStakepoolDetails(
        poolHash: String,
    ): Mono<StakepoolDetailsDto>
}
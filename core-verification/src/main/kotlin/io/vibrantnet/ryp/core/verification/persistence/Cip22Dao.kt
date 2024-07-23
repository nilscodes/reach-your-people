package io.vibrantnet.ryp.core.verification.persistence

import io.ryp.cardano.model.StakepoolDetailsDto
import reactor.core.publisher.Mono

interface Cip22Dao {
    fun getVrfVerificationKeyHashForPool(poolHash: String): Mono<ByteArray>
    fun getStakepoolDetails(poolHash: String): Mono<StakepoolDetailsDto>
}
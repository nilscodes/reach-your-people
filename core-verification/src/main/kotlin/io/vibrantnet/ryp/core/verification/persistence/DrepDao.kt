package io.vibrantnet.ryp.core.verification.persistence

import io.ryp.cardano.model.governance.DRepDelegationInfoDto
import io.ryp.cardano.model.governance.DRepDetailsDto
import reactor.core.publisher.Mono

interface DrepDao {
    fun getDrepDetails(drepId: String): Mono<DRepDetailsDto>
    fun getDrepDetailsForStakeAddress(stakeAddress: String): Mono<DRepDetailsDto>
    fun getActiveDelegationWithoutAmount(drepId: String): List<DRepDelegationInfoDto>
}
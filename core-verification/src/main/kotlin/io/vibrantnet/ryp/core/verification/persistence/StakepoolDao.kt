package io.vibrantnet.ryp.core.verification.persistence

import io.ryp.cardano.model.DelegationInfoDto
import io.ryp.cardano.model.StakepoolDetailsDto
import reactor.core.publisher.Mono

interface StakepoolDao {
    fun getStakepoolDetails(poolHash: String): Mono<StakepoolDetailsDto>
    fun getStakepoolDetailsForStakeAddress(stakeAddress: String): Mono<StakepoolDetailsDto>
    fun getActiveDelegation(poolHash: String): List<DelegationInfoDto>
    fun getActiveDelegationWithoutAmount(poolHash: String): List<DelegationInfoDto>
    fun getDelegationInEpoch(poolHash: String, epochNo: Int): List<DelegationInfoDto>
}
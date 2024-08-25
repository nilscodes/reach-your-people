package io.vibrantnet.ryp.core.verification.persistence

import io.ryp.cardano.model.stakepools.StakepoolDelegationInfoDto
import io.ryp.cardano.model.stakepools.StakepoolDetailsDto
import reactor.core.publisher.Mono

interface StakepoolDao {
    fun getStakepoolDetails(poolHash: String): Mono<StakepoolDetailsDto>
    fun getStakepoolDetailsForStakeAddress(stakeAddress: String): Mono<StakepoolDetailsDto>
    fun getActiveDelegation(poolHash: String): List<StakepoolDelegationInfoDto>
    fun getActiveDelegationWithoutAmount(poolHash: String): List<StakepoolDelegationInfoDto>
    fun getDelegationInEpoch(poolHash: String, epochNo: Int): List<StakepoolDelegationInfoDto>
}
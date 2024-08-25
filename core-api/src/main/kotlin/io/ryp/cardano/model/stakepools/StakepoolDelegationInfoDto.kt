package io.ryp.cardano.model.stakepools

data class StakepoolDelegationInfoDto(
    val poolHash: String,
    val amount: Long,
    val stakeAddress: String
)
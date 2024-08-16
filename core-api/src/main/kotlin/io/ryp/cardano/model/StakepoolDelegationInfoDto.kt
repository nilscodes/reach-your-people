package io.ryp.cardano.model

data class StakepoolDelegationInfoDto(
    val poolHash: String,
    val amount: Long,
    val stakeAddress: String
)
package io.ryp.cardano.model

data class DRepDelegationInfoDto(
    val drepId: String,
    val amount: Long,
    val stakeAddress: String
)
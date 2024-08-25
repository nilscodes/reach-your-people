package io.ryp.cardano.model.governance

data class DRepDelegationInfoDto(
    val drepId: String,
    val amount: Long,
    val stakeAddress: String
)
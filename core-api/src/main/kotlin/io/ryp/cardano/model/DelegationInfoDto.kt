package io.ryp.cardano.model

data class DelegationInfoDto(
    val poolHash: String,
    val amount: Long,
    val stakeAddress: String
)
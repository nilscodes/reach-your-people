package io.ryp.cardano.model

data class SnapshotStakeAddressDto(
    val stakeAddress: String,
    val snapshotType: SnapshotType,
)

enum class SnapshotType {
    STAKEPOOL,
    POLICY,
}

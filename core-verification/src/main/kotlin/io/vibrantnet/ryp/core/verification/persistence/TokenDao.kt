package io.vibrantnet.ryp.core.verification.persistence

import io.hazelnet.cardano.connect.data.token.*
import io.ryp.shared.model.TokenOwnershipInfoWithAssetCount

interface TokenDao {
    fun getMultiAssetCountSnapshotForPolicyId(policyIds: List<PolicyId>): List<TokenOwnershipInfoWithAssetCount>
    fun getMultiAssetListForStakeAddress(stakeAddress: String): List<TokenOwnershipInfoWithAssetCount>
}
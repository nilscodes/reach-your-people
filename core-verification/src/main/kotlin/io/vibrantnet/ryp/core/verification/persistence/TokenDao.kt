package io.vibrantnet.ryp.core.verification.persistence

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount

interface TokenDao {
    fun getMultiAssetCountSnapshotForPolicyId(policyIds: List<PolicyId>): List<TokenOwnershipInfoWithAssetCount>
    fun getMultiAssetListForStakeAddress(stakeAddress: String): List<TokenOwnershipInfoWithAssetCount>
}
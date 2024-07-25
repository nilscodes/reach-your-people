package io.vibrantnet.ryp.core.verification.persistence

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "blockfrost")
class TokenDaoBlockfrost(
    @Qualifier("blockfrostClient") private val blockfrostClient: WebClient,
) : TokenDao {
    override fun getMultiAssetCountSnapshotForPolicyId(policyIds: List<PolicyId>): List<TokenOwnershipInfoWithAssetCount> {
        TODO("Not yet implemented")
    }

    override fun getMultiAssetListForStakeAddress(stakeAddress: String): List<TokenOwnershipInfoWithAssetCount> {
        TODO("Not yet implemented")
    }
}
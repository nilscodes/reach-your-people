package io.vibrantnet.ryp.core.verification.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PartialBlockfrostAssetInfo @JsonCreator constructor(
    @JsonProperty("asset")
    val asset: String,

    @JsonProperty("policy_id")
    val policyId: String,

    @JsonProperty("asset_name")
    val assetName: String?,

    @JsonProperty("fingerprint")
    val fingerprint: String,

    @JsonProperty("quantity")
    val quantity: String,

    @JsonProperty("initial_mint_tx_hash")
    val initialMintTxHash: String,

    @JsonProperty("mint_or_burn_count")
    val mintOrBurnCount: Int,
)

data class TxMetadataEntry @JsonCreator constructor(
    @JsonProperty("label")
    val label: String,

    @JsonProperty("json_metadata")
    val jsonMetadata: Map<String, Any>
)
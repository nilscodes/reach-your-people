package io.vibrantnet.ryp.core.events.model

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

@JsonIgnoreProperties(ignoreUnknown = true)
data class PartialPoolInfo @JsonCreator constructor(
    @JsonProperty("pool_id")
    val poolId: String,

    @JsonProperty("hex")
    val hex: String,

    @JsonProperty("vrf_key")
    val vrfKeyHash: String,

)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PartialPoolMetadata @JsonCreator constructor(
    @JsonProperty("pool_id")
    val poolView: String,

    @JsonProperty("hex")
    val poolHash: String,

    @JsonProperty("ticker")
    val ticker: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("description")
    val description: String,

    @JsonProperty("homepage")
    val homepage: String,
)


data class TxMetadataEntry @JsonCreator constructor(
    @JsonProperty("label")
    val label: String,

    @JsonProperty("json_metadata")
    val jsonMetadata: Map<String, Any>
)
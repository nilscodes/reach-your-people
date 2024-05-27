package io.vibrantnet.ryp.vibrant.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class VerificationDto @JsonCreator constructor(
    var id: Long,
    var amount: Long,
    var blockchain: BlockchainType,
    var address: String,
    var cardanoStakeAddress: String? = null,
    var transactionHash: String? = null,
    var validAfter: Date,
    var validBefore: Date,
    var confirmed: Boolean,
    var confirmedAt: Date? = null,
    var obsolete: Boolean,
    var succeededBy: Long? = null,
)

enum class BlockchainType {
    CARDANO,
    ETHEREUM,
    POLYGON,
}
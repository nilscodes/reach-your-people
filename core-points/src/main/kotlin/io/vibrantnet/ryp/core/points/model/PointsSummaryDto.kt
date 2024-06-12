package io.vibrantnet.ryp.core.points.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class PointsSummaryDto @JsonCreator constructor(

    @JsonProperty("tokenId") val tokenId: Long,

    @JsonProperty("totalPointsClaimed")
    val totalPointsClaimed: Long,

    @JsonProperty("totalPointsAvailable")
    val totalPointsAvailable: Long,

    @JsonProperty("totalPointsSpent")
    val totalPointsSpent: Long,

    @JsonProperty("totalPointsClaimable")
    val totalPointsClaimable: Long
)

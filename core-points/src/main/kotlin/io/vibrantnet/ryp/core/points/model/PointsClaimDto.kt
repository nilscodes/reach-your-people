package io.vibrantnet.ryp.core.points.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class PointsClaimDto @JsonCreator constructor(

    @JsonProperty("points", required = true)
    val points: Long,

    @JsonProperty("category", required = true)
    val category: String,

    @JsonProperty("claimId", required = true)
    val claimId: String,

    @JsonProperty("accountId", required = true)
    val accountId: Long,

    @JsonProperty("tokenId")
    val tokenId: Int,

    @JsonProperty("claimed")
    val claimed: Boolean = false,

    @JsonProperty("projectId")
    val projectId: Long? = null,

    @JsonProperty("expirationTime")
    val expirationTime: OffsetDateTime? = null,

    @JsonProperty("createTime")
    val createTime: OffsetDateTime = OffsetDateTime.now(),

    @JsonProperty("claimTime")
    val claimTime: OffsetDateTime? = null
)


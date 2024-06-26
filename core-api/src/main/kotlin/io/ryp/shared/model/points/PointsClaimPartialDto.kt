package io.ryp.shared.model.points

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class PointsClaimPartialDto @JsonCreator constructor(
    @JsonProperty("claimed")
    val claimed: Boolean? = null,

    @JsonProperty("expirationTime")
    val expirationTime: OffsetDateTime? = null
)
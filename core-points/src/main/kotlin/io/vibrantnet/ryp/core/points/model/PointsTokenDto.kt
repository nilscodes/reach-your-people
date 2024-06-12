package io.vibrantnet.ryp.core.points.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import java.time.OffsetDateTime

data class PointsTokenDto(
    @JsonProperty("id")
    val id: Int? = null,

    @Min(1)
    @JsonProperty("creator", required = true)
    val creator: Long,

    @Pattern(regexp="^[A-Za-z]+$")
    @JsonProperty("name", required = true)
    val name: String,

    @JsonProperty("displayName", required = true)
    val displayName: String,

    @JsonProperty("projectId")
    val projectId: Long? = null,

    @JsonProperty("createTime")
    val createTime: OffsetDateTime = OffsetDateTime.now(),

    @JsonProperty("modifyTime")
    val modifyTime: OffsetDateTime = OffsetDateTime.now(),
) {

}


package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class ShortenedUrlDto(
    @JsonProperty("id")
    val id: String? = null,

    @JsonProperty("shortcode")
    val shortcode: String? = null,

    @JsonProperty("createTime")
    val createTime: OffsetDateTime = OffsetDateTime.now(),

    @JsonProperty("type", required = true)
    val type: Type,

    @JsonProperty("status", required = true)
    val status: Status,

    @JsonProperty("url", required = true)
    val url: String,

    @JsonProperty("projectId")
    val projectId: Long? = null,

    @JsonProperty("views")
    val views: Long = 0,
)

data class ShortenedUrlPartialDto(

    @JsonProperty("status")
    val status: Status? = null,

    @JsonProperty("type")
    val type: Type? = null,

    @JsonProperty("url")
    val url: String? = null
)

enum class Status {
    ACTIVE,
    INACTIVE
}

enum class Type {
    RYP,
    EXTERNAL
}
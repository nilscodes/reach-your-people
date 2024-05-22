package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class BasicAnnouncementDto @JsonCreator constructor(
    @JsonProperty("author", required = true)
    val author: Long,

    @JsonProperty("title", required = true)
    val title: String,

    @JsonProperty("content", required = true)
    val content: String,

    @JsonProperty("link")
    val link: String? = null
) {
    fun toBasicAnnouncementWithIdDto(id: UUID): BasicAnnouncementWithIdDto {
        return BasicAnnouncementWithIdDto(id, author, title, content, link)
    }
}

data class BasicAnnouncementWithIdDto @JsonCreator constructor(
    @JsonProperty("id", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val id: UUID,

    @JsonProperty("author", required = true)
    val author: Long,

    @JsonProperty("title", required = true)
    val title: String,

    @JsonProperty("content", required = true)
    val content: String,

    @JsonProperty("link")
    val link: String? = null
)




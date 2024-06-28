package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class BasicAnnouncementDto @JsonCreator constructor(
    @JsonProperty("author", required = true)
    val author: Long,

    @JsonProperty("type", required = false)
    val type: AnnouncementType = AnnouncementType.STANDARD,

    @JsonProperty("title", required = true)
    val title: String,

    @JsonProperty("content", required = true)
    val content: String,

    @JsonProperty("externalLink")
    val externalLink: String? = null,

    @JsonProperty("policies")
    @field:ValidPolicyList
    val policies: List<String>? = null,
) {
    fun toBasicAnnouncementWithIdDto(id: UUID, link: String): BasicAnnouncementWithIdDto {
        return BasicAnnouncementWithIdDto(id, type, author, title, content, link, externalLink, policies)
    }
}

data class BasicAnnouncementWithIdDto @JsonCreator constructor(
    @JsonProperty("id", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val id: UUID,

    @JsonProperty("type", required = false)
    val type: AnnouncementType = AnnouncementType.STANDARD,

    @JsonProperty("author", required = true)
    val author: Long,

    @JsonProperty("title", required = true)
    val title: String,

    @JsonProperty("content", required = true)
    val content: String,

    @JsonProperty("link")
    val link: String,

    @JsonProperty("externalLink")
    val externalLink: String? = null,

    @JsonProperty("policies")
    @field:ValidPolicyList
    val policies: List<String>? = null,
)

enum class AnnouncementType {
    STANDARD,
    TEST,
}




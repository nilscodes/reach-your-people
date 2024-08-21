package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class MessageDto @JsonCreator constructor(
    @JsonProperty("referenceId")
    val referenceId: String,

    @JsonProperty("announcement")
    val announcement: BasicAnnouncementWithIdDto,

    @JsonProperty("metadata")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val metadata: String? = null,

    @JsonProperty("project")
    val project: BasicProjectDto,

    @JsonProperty("referenceName")
    val referenceName: String? = null,

    @JsonProperty("language")
    val language: String = "en",
)

data class BasicProjectDto @JsonCreator constructor(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("logo")
    val logo: String,

    @JsonProperty("url")
    val url: String,
) {
    constructor(project: ProjectDto) : this(project.id!!, project.name, project.logo, project.url)
}
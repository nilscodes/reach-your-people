package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class AnnouncementJobDto @JsonCreator constructor(
    @JsonProperty("announcementId")
    val announcementId: UUID,

    @JsonProperty("projectId")
    val projectId: Long = 0,

    @JsonProperty("snapshotId")
    val snapshotId: UUID? = null,

    @JsonProperty("global")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val global: List<GlobalAnnouncementAudience> = emptyList(),
)

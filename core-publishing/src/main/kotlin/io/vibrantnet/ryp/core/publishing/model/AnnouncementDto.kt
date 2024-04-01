package io.vibrantnet.ryp.core.publishing.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class AnnouncementDto @JsonCreator constructor(
    @JsonProperty("id")
    val id: UUID,

    @JsonProperty("projectId")
    val projectId: Long,

    @JsonProperty("announcement")
    val announcement: ActivityStream,

    @JsonProperty("status")
    val status: AnnouncementStatus,
)

enum class AnnouncementStatus {
    PREPARED, // Prepared for sending, but not yet due to be sent
    PENDING, // Can be sent immediately
    PUBLISHING, // Currently sending
    PUBLISHED, // Fully sent
    CANCELLED, // Cancelled before or during publishing
}
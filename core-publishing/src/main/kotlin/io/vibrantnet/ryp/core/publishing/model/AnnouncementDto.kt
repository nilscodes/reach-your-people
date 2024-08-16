package io.vibrantnet.ryp.core.publishing.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.ryp.shared.model.Audience
import java.time.OffsetDateTime
import java.util.*

data class AnnouncementDto @JsonCreator constructor(
    @JsonProperty("id")
    val id: UUID,

    @JsonProperty("projectId")
    val projectId: Long,

    @JsonProperty("announcement")
    val announcement: ActivityStream,

    @JsonProperty("status")
    val status: AnnouncementStatus,

    @JsonProperty("shortLink")
    val shortLink: String,

    @JsonProperty("audience")
    val audience: Audience,

    @JsonProperty("statistics")
    val statistics: Statistics,

    @JsonProperty("createdDate")
    val createdDate: OffsetDateTime?,

    @JsonProperty("modifiedDate")
    val modifiedDate: OffsetDateTime?,
)

enum class AnnouncementStatus {
    PREPARED, // Prepared for sending, but not yet due to be sent
    PENDING, // Can be sent immediately
    PUBLISHING, // Currently sending
    PUBLISHED, // Fully sent
    FAILED, // Error during publishing
    CANCELLED, // Cancelled before or during publishing
}
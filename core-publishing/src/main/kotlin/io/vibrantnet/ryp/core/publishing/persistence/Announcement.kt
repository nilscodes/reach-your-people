package io.vibrantnet.ryp.core.publishing.persistence

import com.fasterxml.jackson.annotation.JsonFormat
import io.ryp.shared.model.Audience
import io.vibrantnet.ryp.core.publishing.model.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime
import java.util.*

@Document
data class Announcement(
    @Id
    val id: String,

    @Indexed
    val projectId: Long,

    val announcement: ActivityStream,
    val status: AnnouncementStatus,
    val shortLink: String,
    val audience: Audience,
    val statistics: Statistics = Statistics(),

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val createdDate: OffsetDateTime = OffsetDateTime.now(),

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val modifiedDate: OffsetDateTime = OffsetDateTime.now(),
) {
    fun toDto() = AnnouncementDto(
        id = UUID.fromString(id),
        projectId = projectId,
        announcement = announcement,
        status = status,
        shortLink = shortLink,
        audience = audience,
        statistics = statistics,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
    )
}

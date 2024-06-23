package io.vibrantnet.ryp.core.publishing.persistence

import com.fasterxml.jackson.annotation.JsonFormat
import io.vibrantnet.ryp.core.publishing.model.ActivityStream
import io.vibrantnet.ryp.core.publishing.model.AnnouncementStatus
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Announcement(
    @Id
    val id: String,
    val projectId: Long,
    val announcement: ActivityStream,
    val status: AnnouncementStatus,
    val shortLink: String,

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val createdDate: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val modifiedDate: LocalDateTime = LocalDateTime.now(),
)

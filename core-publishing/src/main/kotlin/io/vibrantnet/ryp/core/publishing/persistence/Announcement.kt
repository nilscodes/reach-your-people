package io.vibrantnet.ryp.core.publishing.persistence

import io.vibrantnet.ryp.core.publishing.model.ActivityStream
import io.vibrantnet.ryp.core.publishing.model.AnnouncementStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Announcement(
    @Id
    val id: String,
    val projectId: Long,
    val announcement: ActivityStream,
    val status: AnnouncementStatus,
)

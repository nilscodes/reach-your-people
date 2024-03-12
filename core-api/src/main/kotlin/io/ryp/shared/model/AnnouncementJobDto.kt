package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*

data class AnnouncementJobDto @JsonCreator constructor(
    val projectId: Long,
    val announcementId: UUID,
    val snapshotId: UUID? = null,
)

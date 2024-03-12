package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator

data class SnapshotRequestDto @JsonCreator constructor(
    val announcementRequest: AnnouncementJobDto,
    val policyIds: List<String>
)
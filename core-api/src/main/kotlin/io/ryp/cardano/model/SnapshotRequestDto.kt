package io.ryp.cardano.model

import com.fasterxml.jackson.annotation.JsonCreator
import io.ryp.shared.model.AnnouncementJobDto

data class SnapshotRequestDto @JsonCreator constructor(
    val announcementRequest: AnnouncementJobDto,
    val policyIds: List<String>,
    val stakepools: List<String>,
)
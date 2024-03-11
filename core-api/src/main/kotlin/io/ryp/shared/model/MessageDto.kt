package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator

data class MessageDto @JsonCreator constructor(
    val referenceId: String,
    val announcement: BasicAnnouncementDto,
)

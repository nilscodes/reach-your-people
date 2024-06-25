package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

data class StatisticsUpdateDto @JsonCreator constructor(
    val announcementId: UUID,
    val statistics: StatisticsDto,
) {
    fun withType(type: String) = StatisticsUpdateWithTypeDto(
        type = type,
        announcementId = announcementId,
        statistics = statistics,
    )
}

data class StatisticsUpdateWithTypeDto @JsonCreator constructor(
    val type: String,
    val announcementId: UUID,
    val statistics: StatisticsDto,
)

data class StatisticsDto @JsonCreator constructor(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val delivered: Long? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val failures: Long? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val views: Long? = null,
)
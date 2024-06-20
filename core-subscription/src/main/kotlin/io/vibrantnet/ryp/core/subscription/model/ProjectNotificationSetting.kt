package io.vibrantnet.ryp.core.subscription.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ProjectNotificationSettingDto(
    @JsonProperty("id")
    val id: Long? = null,

    @JsonProperty("projectId", required = true)
    val projectId: Long,

    @JsonProperty("externalAccountLinkId", required = true)
    val externalAccountLinkId: Long,

    @JsonProperty("createTime")
    val createTime: java.time.OffsetDateTime? = null
)


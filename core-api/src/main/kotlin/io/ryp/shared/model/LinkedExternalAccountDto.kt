package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import java.time.OffsetDateTime
import java.util.*

data class LinkedExternalAccountDto @JsonCreator constructor(
    @JsonProperty("id", required = false)
    val id: Long? = null,

    @JsonProperty("externalAccount", required = true)
    @field:Valid
    val externalAccount: ExternalAccountDto,

    @JsonProperty("role")
    @field:NotNull
    val role: ExternalAccountRole,

    @JsonProperty("linkTime")
    val linkTime: OffsetDateTime? = null,

    @JsonProperty("settings")
    val settings: Set<ExternalAccountSetting> = EnumSet.allOf(ExternalAccountSetting::class.java),

    @JsonProperty("lastConfirmed")
    val lastConfirmed: OffsetDateTime? = null,

    @JsonProperty("lastTested")
    val lastTested: OffsetDateTime? = null,
)

enum class ExternalAccountRole {
    OWNER,
    ADMIN,
    PUBLISHER,
    SUBSCRIBER,
}

enum class ExternalAccountSetting {
    NON_FUNGIBLE_TOKEN_ANNOUNCEMENTS,
    FUNGIBLE_TOKEN_ANNOUNCEMENTS,
    RICH_FUNGIBLE_TOKEN_ANNOUNCEMENTS,
    STAKEPOOL_ANNOUNCEMENTS,
    DREP_ANNOUNCEMENTS,
    DEFAULT_FOR_NOTIFICATIONS,
}

data class LinkedExternalAccountPartialDto @JsonCreator constructor(
    @JsonProperty("settings")
    val settings: EnumSet<ExternalAccountSetting>? = null,

    @JsonProperty("lastConfirmed")
    val lastConfirmed: OffsetDateTime? = null,

    @JsonProperty("lastTested")
    val lastTested: OffsetDateTime? = null,
)
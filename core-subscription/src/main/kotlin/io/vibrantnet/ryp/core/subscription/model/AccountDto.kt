package io.vibrantnet.ryp.core.subscription.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.*

data class AccountDto @JsonCreator constructor(
    @JsonProperty("displayName", required = true)
    @field:Size(min = 1, max = 200)
    val displayName: String,

    @JsonProperty("createTime", required = true)
    val createTime: OffsetDateTime? = null,

    @JsonProperty("id")
    val id: Long? = null,

    @JsonProperty("premiumUntil")
    val premiumUntil: OffsetDateTime? = null,

    @JsonProperty("cardanoSettings")
    val cardanoSettings: Set<CardanoSetting> = EnumSet.allOf(CardanoSetting::class.java),
)

enum class CardanoSetting {
    GOVERNANCE_ACTION_ANNOUNCEMENTS,
}
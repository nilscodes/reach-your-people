package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.ryp.cardano.model.EventNotificationType
import io.ryp.cardano.model.ValidPolicyList
import io.ryp.cardano.model.governance.ValidDRepIDList
import io.ryp.cardano.model.stakepools.ValidStakepoolHashList
import java.util.*

data class BasicAnnouncementDto @JsonCreator constructor(
    @JsonProperty("author", required = true)
    val author: Long,

    @JsonProperty("type", required = false)
    val type: AnnouncementType = AnnouncementType.STANDARD,

    @JsonProperty("title", required = true)
    val title: String,

    @JsonProperty("content", required = true)
    val content: String,

    @JsonProperty("externalLink")
    val externalLink: String? = null,

    @JsonProperty("policies")
    @field:ValidPolicyList
    val policies: List<String>? = null,

    @JsonProperty("stakepools")
    @field:ValidStakepoolHashList
    val stakepools: List<String>? = null,

    @JsonProperty("dreps")
    @field:ValidDRepIDList
    val dreps: List<String>? = null,

    @JsonProperty("global")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val global: List<GlobalAnnouncementAudience> = emptyList(),
) {
    fun toBasicAnnouncementWithIdDto(id: UUID, link: String): BasicAnnouncementWithIdDto {
        return BasicAnnouncementWithIdDto(
            id,
            type,
            author,
            title,
            content,
            link,
            externalLink,
            policies,
            stakepools,
            dreps,
            global,
        )
    }
}

data class BasicAnnouncementWithIdDto @JsonCreator constructor(
    @JsonProperty("id", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val id: UUID,

    @JsonProperty("type", required = false)
    val type: AnnouncementType = AnnouncementType.STANDARD,

    @JsonProperty("author", required = true)
    val author: Long,

    @JsonProperty("title", required = true)
    val title: String,

    @JsonProperty("content", required = true)
    val content: String,

    @JsonProperty("link")
    val link: String,

    @JsonProperty("externalLink")
    val externalLink: String? = null,

    @JsonProperty("policies")
    @field:ValidPolicyList
    val policies: List<String>? = null,

    @JsonProperty("stakepools")
    @field:ValidStakepoolHashList
    val stakepools: List<String>? = null,

    @JsonProperty("dreps")
    @field:ValidDRepIDList
    val dreps: List<String>? = null,

    @JsonProperty("global")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val global: List<GlobalAnnouncementAudience> = emptyList(),

    @JsonProperty("metadata")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val metadata: Map<String, String>? = null,
)

enum class GlobalAnnouncementAudience {
    GOVERNANCE_CARDANO,
}

enum class AnnouncementType {
    STANDARD,
    TEST,
    GOVERNANCE_VOTE,
    STAKEPOOL_RETIREMENT,
    GOVERNANCE_ACTION_NEW_PROPOSAL;

    companion object {
        fun fromEventType(type: EventNotificationType) = when (type) {
            EventNotificationType.GOVERNANCE_VOTE -> GOVERNANCE_VOTE
            EventNotificationType.STAKEPOOL_RETIREMENT -> STAKEPOOL_RETIREMENT
            EventNotificationType.GOVERNANCE_ACTION_NEW_PROPOSAL -> GOVERNANCE_ACTION_NEW_PROPOSAL
        }
    }
}




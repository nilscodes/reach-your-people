package io.vibrantnet.ryp.core.subscription.persistence

import io.vibrantnet.ryp.core.subscription.model.ProjectNotificationSettingDto
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "project_notification_settings", uniqueConstraints = [
    UniqueConstraint(columnNames = ["project_id", "external_account_link_id"])
])
class ProjectNotificationSetting(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_settings_id")
    val id: Long? = null,

    @Column(name = "project_id", nullable = false)
    val projectId: Long ,

    @ManyToOne
    @JoinColumn(name = "external_account_link_id", nullable = false)
    val linkedExternalAccount: LinkedExternalAccount,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    var createTime: OffsetDateTime = OffsetDateTime.now(),
) {
    fun toDto() = ProjectNotificationSettingDto(
        id = id,
        projectId = projectId,
        externalAccountLinkId = linkedExternalAccount.id!!,
        createTime = createTime,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProjectNotificationSetting) return false

        if (projectId != other.projectId) return false
        if (linkedExternalAccount != other.linkedExternalAccount) return false
        if (createTime != other.createTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 31 * projectId.hashCode()
        result = 31 * result + linkedExternalAccount.hashCode()
        result = 31 * result + createTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "ProjectNotificationSetting(id=$id, projectId=$projectId, linkedExternalAccount=$linkedExternalAccount, createTime=$createTime)"
    }


}
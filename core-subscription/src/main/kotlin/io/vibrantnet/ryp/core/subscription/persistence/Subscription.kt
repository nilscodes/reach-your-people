package io.vibrantnet.ryp.core.subscription.persistence

import io.ryp.shared.model.SubscriptionStatus
import io.vibrantnet.ryp.core.subscription.model.ProjectSubscriptionDto
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class Subscription(
    @Column(name = "project_id")
    var projectId: Long,

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    var status: SubscriptionStatus,
) {
    fun toDto() = ProjectSubscriptionDto(
        projectId = projectId,
        currentStatus = status,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Subscription) return false

        if (projectId != other.projectId) return false

        return true
    }

    override fun hashCode(): Int {
        return projectId.hashCode()
    }

    override fun toString(): String {
        return "Subscription(projectId=$projectId, status=$status)"
    }

}
package io.vibrantnet.ryp.core.subscription.persistence

import io.ryp.shared.model.ProjectRole
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class ProjectRoleAssignment(
    @Column(name = "role")
    @Enumerated(EnumType.ORDINAL)
    var role: ProjectRole,

    @Column(name = "account_id")
    var accountId: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProjectRoleAssignment) return false

        if (role != other.role) return false
        if (accountId != other.accountId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = role.hashCode()
        result = 31 * result + accountId.hashCode()
        return result
    }

    override fun toString(): String {
        return "ProjectRoleAssignment(role=$role, accountId=$accountId)"
    }


}
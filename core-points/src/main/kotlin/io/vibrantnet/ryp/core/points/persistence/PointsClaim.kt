package io.vibrantnet.ryp.core.points.persistence

import io.vibrantnet.ryp.core.points.model.PointsClaimDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "claims")
class PointsClaim(
    @Id
    @Column(name = "claim_id")
    var claimId: String,

    @Column(name = "points")
    var points: Long,

    @Column(name = "category")
    var category: String,

    @Column(name = "account_id")
    var accountId: Long,

    @Column(name = "token_id")
    var tokenId: Int,

    @Column(name = "claimed")
    var claimed: Boolean = false,

    @Column(name = "project_id")
    var projectId: Long? = null,

    @Column(name = "expiration_time")
    var expirationTime: OffsetDateTime? = null,

    @Column(name = "create_time", updatable = false)
    var createTime: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "claim_time")
    var claimTime: OffsetDateTime? = null,
) {
    fun toDto() = PointsClaimDto(
        points = points,
        category = category,
        claimId = claimId,
        accountId = accountId,
        tokenId = tokenId,
        claimed = claimed,
        projectId = projectId,
        expirationTime = expirationTime,
        createTime = createTime,
        claimTime = claimTime,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PointsClaim) return false

        if (points != other.points) return false
        if (category != other.category) return false
        if (accountId != other.accountId) return false
        if (tokenId != other.tokenId) return false
        if (claimed != other.claimed) return false
        if (projectId != other.projectId) return false
        if (expirationTime != other.expirationTime) return false
        if (createTime != other.createTime) return false
        if (claimTime != other.claimTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = points.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + accountId.hashCode()
        result = 31 * result + tokenId.hashCode()
        result = 31 * result + claimed.hashCode()
        result = 31 * result + (projectId?.hashCode() ?: 0)
        result = 31 * result + (expirationTime?.hashCode() ?: 0)
        result = 31 * result + createTime.hashCode()
        result = 31 * result + claimTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "PointsClaim(points=$points, category='$category', claimId='$claimId', accountId=$accountId, tokenId=$tokenId, claimed=$claimed, projectId=$projectId, expirationTime=$expirationTime, createTime=$createTime, claimTime=$claimTime)"
    }

    fun hasExpired() = expirationTime?.isBefore(OffsetDateTime.now()) ?: false

}
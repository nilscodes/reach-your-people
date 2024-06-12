package io.vibrantnet.ryp.core.points.persistence

import io.vibrantnet.ryp.core.points.model.PointsTokenDto
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "tokens")
class PointsToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int? = null,

    @Column(name = "name")
    var name: String,

    @Column(name = "display_name")
    var displayName: String,

    @Column(name = "creator")
    var creator: Long,

    @Column(name = "project_id")
    var projectId: Long?,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    var createTime: OffsetDateTime = OffsetDateTime.now(),

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_time")
    var modifyTime: OffsetDateTime = OffsetDateTime.now(),
) {
    fun toDto() = PointsTokenDto(
        id = id!!,
        name = name,
        displayName = displayName,
        creator = creator,
        projectId = projectId,
        createTime = createTime,
        modifyTime = modifyTime,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PointsToken) return false

        if (name != other.name) return false
        if (displayName != other.displayName) return false
        if (creator != other.creator) return false
        if (projectId != other.projectId) return false
        if (createTime != other.createTime) return false
        if (modifyTime != other.modifyTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + creator.hashCode()
        result = 31 * result + (projectId?.hashCode() ?: 0)
        result = 31 * result + createTime.hashCode()
        result = 31 * result + modifyTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "PointsToken(id=$id, name='$name', displayName='$displayName', creator=$creator, projectId=$projectId, createTime=$createTime, modifyTime=$modifyTime)"
    }

}


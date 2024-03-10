package io.vibrantnet.ryp.core.subscription.persistence

import io.ryp.shared.model.ProjectCategory
import io.ryp.shared.model.ProjectDto
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "projects")
class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="project_id")
    var id: Long? = null,

    @Column(name = "name")
    var name: String?,

    @Column(name = "logo", columnDefinition = "TEXT")
    var logo: String?,

    @Column(name = "url")
    var url: String?,

    @Column(name = "description")
    var description: String?,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registration_time", updatable = false)
    var registrationTime: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "category")
    @Enumerated(EnumType.ORDINAL)
    var category: ProjectCategory,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_tags", joinColumns = [JoinColumn(name = "project_id")])
    @Column(name = "tag")
    var tags: MutableSet<String> = mutableSetOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_policies", joinColumns = [JoinColumn(name = "project_id")])
    var policies: MutableSet<Policy> = mutableSetOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_roles", joinColumns = [JoinColumn(name = "project_id")])
    var roles: MutableSet<ProjectRoleAssignment> = mutableSetOf(),
) {
    fun toDto() = ProjectDto(
        id = id,
        name = name!!,
        logo = logo!!,
        url = url!!,
        description = description!!,
        registrationTime = registrationTime,
        category = category,
        tags = tags,
        policies = policies.map { it.toDto() }.toSet(),
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Project) return false

        if (name != other.name) return false
        if (logo != other.logo) return false
        if (url != other.url) return false
        if (description != other.description) return false
        if (registrationTime != other.registrationTime) return false
        if (category != other.category) return false
        if (tags != other.tags) return false
        if (policies != other.policies) return false
        if (roles != other.roles) return false

        return true
    }

    override fun hashCode(): Int {
        var result = (name?.hashCode() ?: 0)
        result = 31 * result + (logo?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + registrationTime.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + tags.hashCode()
        result = 31 * result + policies.hashCode()
        result = 31 * result + roles.hashCode()
        return result
    }

    override fun toString(): String {
        return "Project(id=$id, name=$name, logo=$logo, url=$url, description=$description, registrationTime=$registrationTime, category='$category', tags=$tags, policies=$policies, roles=$roles)"
    }


}

package io.vibrantnet.ryp.core.subscription.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository: CrudRepository<Project, Long> {
    fun findDistinctByRolesAccountId(accountId: Long): List<Project>
}
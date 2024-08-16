package io.vibrantnet.ryp.core.subscription.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository: CrudRepository<Project, Long> {
    fun findDistinctByRolesAccountId(accountId: Long): List<Project>

    fun findByPoliciesPolicyIdIn(policyIds: Collection<String>): List<Project>
    fun findByStakepoolsPoolHashIn(poolHashes: Collection<String>): List<Project>
    fun findByDrepsDrepIdIn(drepIds: Collection<String>): List<Project>
}
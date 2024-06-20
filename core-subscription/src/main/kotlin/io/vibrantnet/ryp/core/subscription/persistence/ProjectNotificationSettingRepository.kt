package io.vibrantnet.ryp.core.subscription.persistence

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProjectNotificationSettingRepository: CrudRepository<ProjectNotificationSetting, Long> {
    @Query("SELECT p FROM ProjectNotificationSetting p JOIN p.linkedExternalAccount l WHERE l.accountId = :accountId AND p.projectId = :projectId")
    fun findByAccountIdAndProjectId(@Param("accountId") accountId: Long, @Param("projectId") projectId: Long): List<ProjectNotificationSetting>
}
package io.vibrantnet.ryp.core.subscription.persistence

import io.vibrantnet.ryp.core.subscription.model.SubscriptionStatus
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface AccountRepository: CrudRepository<Account, Long> {
    fun findByLinkedExternalAccountsExternalAccountId(externalAccountId: Long): List<Account>

    @Query("SELECT ea FROM Account a JOIN a.linkedExternalAccounts lea JOIN a.subscriptions s JOIN lea.externalAccount ea WHERE s.projectId = :projectId AND s.status = :status")
    fun findExternalAccountsByProjectIdAndSubscriptionStatus(
        @Param("projectId") projectId: Long,
        @Param("status") status: SubscriptionStatus,
    ): List<ExternalAccount>

}
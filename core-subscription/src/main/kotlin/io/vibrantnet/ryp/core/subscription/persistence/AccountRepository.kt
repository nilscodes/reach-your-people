package io.vibrantnet.ryp.core.subscription.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository: CrudRepository<Account, Long> {
    fun findByLinkedExternalAccountsExternalAccountId(externalAccountId: Long): List<Account>
}
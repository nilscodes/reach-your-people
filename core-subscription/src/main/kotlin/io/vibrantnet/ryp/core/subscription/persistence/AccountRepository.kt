package io.vibrantnet.ryp.core.subscription.persistence

import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface AccountRepository: CrudRepository<Account, Long> {
    fun findByLinkedExternalAccountsExternalAccountId(externalAccountId: Long): List<Account>

    @Modifying
    @Transactional
    @Query("UPDATE accounts SET cardano_settings = CAST(:settings AS bit(16)) WHERE account_id = :id", nativeQuery = true)
    fun updateCardanoSettings(@Param("id") id: Long, @Param("settings") settings: String)
}
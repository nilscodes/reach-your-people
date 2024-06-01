package io.vibrantnet.ryp.core.subscription.persistence

import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
interface LinkedExternalAccountRepository: CrudRepository<LinkedExternalAccount, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE linked_external_accounts SET settings = CAST(:settings AS bit(16)) WHERE link_id = :id", nativeQuery = true)
    fun updateSettings(@Param("id") id: Long, @Param("settings") settings: String)
}
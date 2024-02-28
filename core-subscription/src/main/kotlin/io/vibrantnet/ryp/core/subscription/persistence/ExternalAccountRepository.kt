package io.vibrantnet.ryp.core.subscription.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ExternalAccountRepository: CrudRepository<ExternalAccount, Long> {
    fun findByTypeAndReferenceId(providerType: String, referenceId: String): Optional<ExternalAccount>
}
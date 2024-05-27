package io.vibrantnet.ryp.core.subscription.persistence

import io.vibrantnet.ryp.core.subscription.model.SubscriptionStatus
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ExternalAccountRepository: CrudRepository<ExternalAccount, Long> {
    fun findByTypeAndReferenceId(providerType: String, referenceId: String): Optional<ExternalAccount>

    // TODO This will want a decent set of test cases with an H2 database
    // Will likely need some sort of splitting of wallet IDs once a certain threshold is met
    // TODO write test for the bug RYP-118 - wallet excluded if blocking other project
    @Query(value = "SELECT DISTINCT a.account_id " +
            "FROM accounts a " +
            "JOIN linked_external_accounts lea " +
            "ON a.account_id = lea.account_id " +
            "JOIN external_accounts ea " +
            "ON ea.external_account_id = lea.external_account_id " +
            "LEFT OUTER JOIN (" +
            "  SELECT *" +
            "  FROM subscriptions" +
            "  WHERE project_id = :projectId" +
            ") s " +
            "ON a.account_id = s.account_id " +
            "WHERE ea.account_type = 'cardano' " +
            "AND ea.external_reference_id IN :walletIds " +
            "AND (s.project_id IS NULL OR s.status NOT IN :blockingStatuses)", nativeQuery = true)
    fun findEligibleAccountsByWallet(projectId: Long, walletIds: List<String>, blockingStatuses: List<SubscriptionStatus>): List<Int>

    @Query("SELECT DISTINCT lea.externalAccount " +
            "FROM Account a " +
            "JOIN a.linkedExternalAccounts lea " +
            "WHERE a.id IN :accountIds AND lea.externalAccount.type NOT IN :nonMessagingTypes")
    fun findMessagingExternalAccountsForAccounts(accountIds: List<Int>, nonMessagingTypes: List<String>): List<ExternalAccount>
}
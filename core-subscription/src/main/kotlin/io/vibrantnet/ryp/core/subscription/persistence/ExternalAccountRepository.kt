package io.vibrantnet.ryp.core.subscription.persistence

import io.ryp.shared.model.SubscriptionStatus
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ExternalAccountRepository: CrudRepository<ExternalAccount, Long> {
    fun findByTypeAndReferenceId(providerType: String, referenceId: String): Optional<ExternalAccount>
    fun findByTypeAndReferenceName(providerType: String, referenceName: String): Optional<ExternalAccount>

    // TODO This will want a decent set of test cases with an H2 database
    // Will likely need some sort of splitting of wallet IDs once a certain threshold is met
    // TODO write test for the bug RYP-118 - wallet excluded if blocking other project
    @Query(value = """
        SELECT DISTINCT a.account_id 
            FROM "accounts" a 
            JOIN "linked_external_accounts" lea 
            ON a.account_id = lea.account_id 
            JOIN "external_accounts" ea 
            ON ea.external_account_id = lea.external_account_id 
            LEFT OUTER JOIN (
              SELECT *
              FROM "subscriptions"
              WHERE project_id = :projectId
            ) s 
            ON a.account_id = s.account_id 
            WHERE ea.account_type = 'cardano' 
            AND ea.external_reference_id IN :walletIds 
            AND (s.project_id IS NULL OR s.status NOT IN :blockingStatuses)
            AND lea.settings & cast(:requiredSettings as bit(16)) = cast(:requiredSettings as bit(16))
""", nativeQuery = true)
    fun findEligibleAccountsByWallet(projectId: Long, walletIds: List<String>, blockingStatuses: List<SubscriptionStatus>, requiredSettings: String): List<Int>

    @Query(
        """
SELECT DISTINCT
    ea.external_account_id as id,
    ea.external_reference_id as reference_id,
    ea.external_reference_name as reference_name,
    ea.display_name as display_name,
    ea.registration_time as registration_time,
    ea.account_type as type,
    ea.metadata as metadata,
    lea.account_id as account_id
FROM "accounts" a
         JOIN "linked_external_accounts" lea
              ON a.account_id = lea.account_id
         JOIN "external_accounts" ea
              ON ea.external_account_id = lea.external_account_id
WHERE a.account_id IN :accountIds
  AND ea.account_type NOT IN :nonMessagingTypes
  AND (
    EXISTS (SELECT 1
            FROM "project_notification_settings" pns
            WHERE pns.external_account_link_id = lea.link_id
              AND pns.project_id = :projectId)
        OR (
        NOT EXISTS (SELECT 1
                    FROM "project_notification_settings" pns
                             JOIN linked_external_accounts lea2
                                  on pns.external_account_link_id = lea2.link_id
                    WHERE pns.project_id = :projectId
                      AND lea2.account_id = lea.account_id)
            AND lea.settings & B'0000000000100000' = B'0000000000100000'
        )
    )    
        """, nativeQuery = true
    )
    fun findMessagingExternalAccountsForProjectAndAccounts(projectId: Long, accountIds: List<Int>, nonMessagingTypes: List<String>): List<ExternalAccountWithAccountProjection>

    @Query(
        """
SELECT DISTINCT 
    ea.external_account_id as id,
    ea.external_reference_id as reference_id,
    ea.external_reference_name as reference_name,
    ea.display_name as display_name,
    ea.registration_time as registration_time,
    ea.account_type as type,
    ea.metadata as metadata,
    lea.account_id as account_id
FROM "external_accounts" ea
JOIN "linked_external_accounts" lea
ON ea.external_account_id = lea.external_account_id
WHERE lea.account_id IN (SELECT s.account_id
                         FROM "subscriptions" s
                         WHERE s.project_id = :projectId
                           AND s.status = :status)
  AND (
    EXISTS (SELECT 1
            FROM "project_notification_settings" pns
            WHERE pns.external_account_link_id = lea.link_id
              AND pns.project_id = :projectId)
        OR (
        NOT EXISTS (SELECT 1
                    FROM "project_notification_settings" pns
                             JOIN linked_external_accounts lea2
                                  on pns.external_account_link_id = lea2.link_id
                    WHERE pns.project_id = :projectId
                      AND lea2.account_id = lea.account_id)
            AND lea.settings & B'0000000000100000' = B'0000000000100000'
        )
    )
            """, nativeQuery = true)
    fun findExternalAccountsByProjectIdAndSubscriptionStatus(
        @Param("projectId") projectId: Long,
        @Param("status") status: SubscriptionStatus,
    ): List<ExternalAccountWithAccountProjection>

    @Query(value = """
SELECT DISTINCT 
    ea.external_account_id as id,
    ea.external_reference_id as reference_id,
    ea.external_reference_name as reference_name,
    ea.display_name as display_name,
    ea.registration_time as registration_time,
    ea.account_type as type,
    ea.metadata as metadata,
    lea.account_id as account_id
FROM "accounts" a
JOIN "linked_external_accounts" lea
ON a.account_id = lea.account_id
JOIN "external_accounts" ea
ON ea.external_account_id = lea.external_account_id
            WHERE a.cardano_settings & cast(:requiredSettings as bit(16)) = cast(:requiredSettings as bit(16))
            AND lea.role=:requiredRole
            AND lea.settings & B'0000000000100000' = B'0000000000100000'
""", nativeQuery = true)
    fun findExternalAccountsForGlobalAudience(requiredRole: Int, requiredSettings: String): List<ExternalAccountWithAccountProjection>
}
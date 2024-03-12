package io.vibrantnet.ryp.core.subscription.persistence

import io.vibrantnet.ryp.core.subscription.model.SubscriptionStatus
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import org.springframework.transaction.annotation.Transactional

@Repository
class ExternalAccountCustomRepository(
    @PersistenceContext
    private val entityManager: EntityManager
) {

    // TODO This will want a decent set of test cases with an H2 database
    @Transactional
    fun findEligibleExternalAccounts(projectId: Long, walletIds: List<String>): List<ExternalAccount> {
        val cb: CriteriaBuilder = entityManager.criteriaBuilder

        // Subquery to find accounts linked to "cardano" external accounts and matching wallets
        val subquery: CriteriaQuery<Account> = cb.createQuery(Account::class.java)
        val accountRootSub: Root<Account> = subquery.from(Account::class.java)
        val linkedExternalAccountsSub = accountRootSub.join<Account, LinkedExternalAccount>("linkedExternalAccounts")
        val externalAccountSub = linkedExternalAccountsSub.join<LinkedExternalAccount, ExternalAccount>("externalAccount")
        val subscriptionJoinSub = accountRootSub.join<Account, Subscription>("subscriptions", jakarta.persistence.criteria.JoinType.LEFT)

        // Conditions for the subquery
        val cardanoTypeCondition = cb.equal(externalAccountSub.get<String>("type"), "cardano")
        val walletIdConditionSub = externalAccountSub.get<String>("referenceId").`in`(walletIds)
        val subscriptionConditionSub = cb.or(
            cb.isNull(subscriptionJoinSub.get<Subscription>("projectId")),
            cb.and(
                cb.equal(subscriptionJoinSub.get<Subscription>("projectId"), projectId),
                cb.notEqual(subscriptionJoinSub.get<Subscription>("status"), SubscriptionStatus.BLOCKED)
            )
        )

        subquery.select(accountRootSub)
            .where(cb.and(cardanoTypeCondition, walletIdConditionSub, subscriptionConditionSub))
            .distinct(true)

        // Main query to fetch non-"cardano" linked external accounts of the identified accounts
        val mainQuery: CriteriaQuery<ExternalAccount> = cb.createQuery(ExternalAccount::class.java)
        val accountRootMain: Root<Account> = mainQuery.from(Account::class.java)
        val linkedExternalAccountsMain = accountRootMain.join<Account, LinkedExternalAccount>("linkedExternalAccounts")
        val externalAccountMain = linkedExternalAccountsMain.get<LinkedExternalAccount>("externalAccount")

        // Exclude "cardano" type and match accounts from subquery
        val nonCardanoCondition = cb.notEqual(externalAccountMain.get<String>("type"), "cardano")
        val accountInSubqueryCondition = accountRootMain.`in`(entityManager.createQuery(subquery).resultList)
        val externalAccountJoinMain = linkedExternalAccountsMain.join<LinkedExternalAccount, ExternalAccount>("externalAccount")

        // Now selecting ExternalAccount entities
        mainQuery.select(externalAccountJoinMain)
            .where(cb.and(nonCardanoCondition, accountInSubqueryCondition))
            .distinct(true)

        return entityManager.createQuery(mainQuery).resultList
    }

}

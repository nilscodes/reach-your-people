package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.model.LinkedExternalAccountDto
import io.vibrantnet.ryp.core.subscription.model.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AccountsApiService {

    /**
     * POST /accounts : Create New Account
     * Create a new account.
     *
     * @param accountDto Post the necessary fields for the API to create a new user. (required)
     * @return Account Created (status code 200)
     *         or Missing Required Information (status code 400)
     * @see AccountsApi#createAccount
     */
    fun createAccount(accountDto: AccountDto): Mono<AccountDto>

    /**
     * GET /accounts/{accountId} : Get Account Info by numeric ID
     * Retrieve the information of the account with the matching account ID.
     *
     * @param accountId The ID of an account (required)
     * @return User Found (status code 200)
     *         or User Not Found (status code 404)
     * @see AccountsApi#getAccountById
     */
    fun getAccountById(accountId: Long): Mono<AccountDto>

    /**
     * GET /accounts/{providerType}/{referenceId} : Find Account by External Account Provider
     * Look up an account by provider and the corresponding reference ID, to see if the account is used somewhere, without knowing the internal ID of the linked OWNER account
     *
     * @param providerType The provider or integration type for an external account (required)
     * @param referenceId The reference ID used to identify the user in the external provider/integration (required)
     * @return A matching account for the provider/reference ID combination provided. (status code 200)
     *         or No account under that provider type and reference ID was found. (status code 404)
     * @see AccountsApi#findAccountByProviderAndReferenceId
     */
    fun findAccountByProviderAndReferenceId(providerType: String, referenceId: String): Mono<AccountDto>

    /**
     * GET /accounts/{accountId}/externalaccounts : Show linked external accounts
     *
     * @param accountId  (required)
     * @return List of linked external accounts (status code 200)
     * @see AccountsApi#getLinkedExternalAccounts
     */
    fun getLinkedExternalAccounts(accountId: Long): Flux<LinkedExternalAccountDto>

    /**
     * PUT /accounts/{accountId}/externalaccounts/{externalAccountId} : Link existing external account
     * Links an existing external account to this account (if possible)
     *
     * @param externalAccountId  (required)
     * @param accountId  (required)
     * @return The linked external account (status code 200)
     * @see AccountsApi#linkExternalAccount
     */
    fun linkExternalAccount(externalAccountId: Long, accountId: Long): Mono<LinkedExternalAccountDto>

    /**
     * DELETE /accounts/{accountId}/externalaccounts/{externalAccountId} : Unlink external account
     * Unlink the external account from this account
     *
     * @param accountId  (required)
     * @param externalAccountId  (required)
     * @return Unlinking successful (status code 204)
     * @see AccountsApi#unlinkExternalAccount
     */
    fun unlinkExternalAccount(accountId: Long, externalAccountId: Long)

    /**
     * PATCH /accounts/{accountId} : Update Account Information
     * Update the information of an existing user.
     *
     * @param accountId The numeric ID or UUID of an account (required)
     * @param accountPartialDto Patch user properties to update. (required)
     * @return Account Updated (status code 200)
     *         or User Not Found (status code 404)
     *         or Email Already Taken (status code 409)
     * @see AccountsApi#updateAccountById
     */
    fun updateAccountById(accountId: Long, accountPartialDto: AccountPartialDto): Mono<AccountDto>

    /**
     * PUT /accounts/{accountId}/subscriptions/projects/{projectId} : Add explicit subscription for this account and this project
     *
     * @param accountId The numeric ID of an account (required)
     * @param projectId The numeric ID of a Project (required)
     * @param newSubscription  (required)
     * @return OK (status code 200)
     * @see AccountsApi#subscribeAccountToProject
     */
    fun subscribeAccountToProject(accountId: Long, projectId: Long, newSubscription: NewSubscriptionDto): Mono<NewSubscriptionDto>

    /**
     * DELETE /accounts/{accountId}/subscriptions/projects/{projectId} : Remove explicit subscription for this account and project
     *
     * @param accountId The numeric ID of an account (required)
     * @param projectId The numeric ID of a Project (required)
     * @return Successful removal of explict subscriptioon status, rev (status code 204)
     * @see AccountsApi#unsubscribeAccountFromProject
     */
    fun unsubscribeAccountFromProject(accountId: Long, projectId: Long): Mono<Unit>

    /**
     * GET /accounts/{accountId}/subscriptions : Get all subscriptions for an account
     *
     * @param accountId The numeric ID of an account (required)
     * @return All explicit subscriptions (subscribed and blocked) for this account. (status code 200)
     * @see AccountsApi#getAllSubscriptionsForAccount
     */
    fun getAllSubscriptionsForAccount(accountId: Long): Flux<ProjectSubscriptionDto>
}

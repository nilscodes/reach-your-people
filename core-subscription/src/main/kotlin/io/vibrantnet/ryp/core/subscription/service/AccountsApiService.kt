package io.vibrantnet.ryp.core.subscription.service

import io.vibrantnet.ryp.core.subscription.model.AccountDto
import io.vibrantnet.ryp.core.subscription.model.AccountPartialDto
import io.vibrantnet.ryp.core.subscription.model.LinkedExternalAccountDto
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
}

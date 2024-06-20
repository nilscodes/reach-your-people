package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.model.LinkedExternalAccountDto
import io.ryp.shared.model.LinkedExternalAccountPartialDto
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
    fun createAccount(accountDto: AccountDto, referredBy: Long?): Mono<AccountDto>

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
     * PATCH /accounts/{accountId}/externalaccounts/{externalAccountId} : Update settings for a linked external account
     * Updates an existing linked external account and changes its settings
     *
     * @param accountId The numeric ID of an account (required)
     * @param externalAccountId The numeric ID of an external account (required)
     * @param updateLinkedExternalAccountRequest  (required)
     * @return The updated linked external account (status code 200)
     * @see AccountsApi#updateLinkedExternalAccount
     */
    fun updateLinkedExternalAccount(accountId: Long, externalAccountId: Long, linkedExternalAccountPartial: LinkedExternalAccountPartialDto): Mono<LinkedExternalAccountDto>

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

    /**
     * GET /accounts/{accountId}/settings : Get settings for this account
     * Get all settings that this account has configured.
     *
     * @param accountId The numeric ID of an account (required)
     * @return All settings for this account (status code 200)
     * @see AccountsApi#getSettingsForAccount
     */
    fun getSettingsForAccount(accountId: Long): Mono<SettingsDto>

    /**
     * PUT /accounts/{accountId}/settings/{settingName} : Update account setting
     * Create or update a single account setting with the provided value
     *
     * @param accountId The numeric ID of an account (required)
     * @param settingName  (required)
     * @param setting  (optional)
     * @return AccountSetting (status code 200)
     * @see AccountsApi#updateAccountSetting
     */
    fun updateAccountSetting(accountId: Long, settingName: String, setting: SettingDto): Mono<SettingDto>

    /**
     * DELETE /accounts/{accountId}/settings/{settingName} : Delete account setting
     * Delete a single account setting
     *
     * @param accountId The numeric ID of an account (required)
     * @param settingName  (required)
     * @return Successful deletion (status code 204)
     * @see AccountsApi#deleteAccountSetting
     */
    fun deleteAccountSetting(accountId: Long, settingName: String): Mono<Unit>

    /**
     * GET /accounts/{accountId}/projects/{projectId}/notifications : Get project notification settings
     * Get the current notification settings for this account on this project.
     *
     * @param accountId The numeric ID of an account (required)
     * @param projectId The numeric ID of a Project (required)
     * @return The current notification settings (status code 200)
     * @see AccountsApi#getNotificationsSettingsForAccountAndProject
     */
    fun getNotificationsSettingsForAccountAndProject(accountId: Long, projectId: Long): Flux<ProjectNotificationSettingDto>

    /**
     * PUT /accounts/{accountId}/projects/{projectId}/notifications : Update the notification settings
     * Update the notification settings for this account on this project. Will remove any invalid settings, like notification settings for non-linked external accounts or external accounts that cannot receive notifications.
     *
     * @param accountId The numeric ID of an account (required)
     * @param projectId The numeric ID of a Project (required)
     * @param projectNotificationSettings  (required)
     * @return The confirmed notification settings (status code 200)
     * @see AccountsApi#updateNotificationsSettingsForAccountAndProject
     */
    fun updateNotificationsSettingsForAccountAndProject(accountId: Long, projectId: Long, projectNotificationSettings: List<ProjectNotificationSettingDto>): Flux<ProjectNotificationSettingDto>
}

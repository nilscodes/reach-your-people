package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.model.ExternalAccountDto
import io.vibrantnet.ryp.core.subscription.model.UnsubscribeFromEmailRequest
import reactor.core.publisher.Mono

interface ExternalAccountsApiService {

    /**
     * POST /externalaccounts : Create External Account
     *
     * @param externalAccountDto  (required)
     * @return External account data (status code 201)
     * @see ExternalAccountsApi#createExternalAccount
     */
    fun createExternalAccount(externalAccountDto: ExternalAccountDto): Mono<ExternalAccountDto>

    /**
     * GET /externalaccounts/{providerType}/{referenceId} : Find External Account by Provider
     * Look up an external account by provider and the corresponding reference ID, to see if the external account is used somewhere, without knowing the internal ID.
     *
     * @param providerType The provider or integration type for an external account (required)
     * @param referenceId The reference ID used to identify the user in the external provider/integration (required)
     * @return A matching external account for the provider/reference ID combination provided. (status code 200)
     *         or No external account under that provider type and reference ID was found. (status code 404)
     * @see ExternalAccountsApi#findExternalAccountByProviderAndReferenceId
     */
    fun findExternalAccountByProviderAndReferenceId(providerType: String, referenceId: String): Mono<ExternalAccountDto>

    /**
     * POST /email/unsubscribe : Unsubscribe any email address from the service
     * To comply with spam protection laws, we allow an email to unsubscribe even if the person is not logged in or can confirm ownership of the email address.
     *
     * @param unsubscribeFromEmailRequest
     * @return Always returns 204 and no content, even if the email address did not actually exist (status code 204)
     * @see EmailApi#unsubscribeFromEmail
     */
    fun unsubscribeFromEmail(unsubscribeFromEmailRequest: UnsubscribeFromEmailRequest): Mono<Unit>
}

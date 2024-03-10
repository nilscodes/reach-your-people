package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.model.ExternalAccountDto
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
}

package io.vibrantnet.ryp.core.subscription.service

import io.vibrantnet.ryp.core.subscription.model.ExternalAccountDto
import reactor.core.publisher.Mono

fun interface ExternalAccountsApiService {

    /**
     * POST /externalaccounts : Create External Account
     *
     * @param externalAccountDto  (required)
     * @return External account data (status code 201)
     * @see ExternalAccountsApi#createExternalAccount
     */
    fun createExternalAccount(externalAccountDto: ExternalAccountDto): Mono<ExternalAccountDto>
}

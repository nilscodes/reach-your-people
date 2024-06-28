package io.vibrantnet.ryp.core.publishing.service

import io.ryp.shared.model.BasicAnnouncementWithIdDto
import reactor.core.publisher.Mono

fun interface AccountsApiService {

    /**
     * POST /accounts/{accountId}/externalaccounts/{externalAccountId}/test : Send test announcement to account
     *
     * @param accountId The numeric ID of an account (required)
     * @param externalAccountId The numeric ID of an external account (required)
     * @return OK (status code 201)
     * @see AccountsApi#sendTestAnnouncement
     */
    fun sendTestAnnouncement(accountId: kotlin.Long, externalAccountId: Long): Mono<BasicAnnouncementWithIdDto>
}

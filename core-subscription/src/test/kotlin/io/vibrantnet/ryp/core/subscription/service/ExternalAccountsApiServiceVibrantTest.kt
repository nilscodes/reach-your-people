package io.vibrantnet.ryp.core.subscription.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.vibrantnet.ryp.core.subscription.model.AccountDto
import io.vibrantnet.ryp.core.subscription.model.ExternalAccountDto
import io.vibrantnet.ryp.core.subscription.persistence.Account
import io.vibrantnet.ryp.core.subscription.persistence.AccountRepository
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccount
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

internal class ExternalAccountsApiServiceVibrantTest {
    @Test
    fun `creating an external account works`() {
        val accountRepository = mockk<ExternalAccountRepository>();
        val service = ExternalAccountsApiServiceVibrant(accountRepository)
        val slot = slot<ExternalAccount>()
        every { accountRepository.save(capture(slot)) } answers {
            ExternalAccount(
                id = 9,
                referenceId = firstArg<ExternalAccount>().referenceId,
                referenceName = firstArg<ExternalAccount>().referenceName,
                type = firstArg<ExternalAccount>().type,
                registrationTime = firstArg<ExternalAccount>().registrationTime,
            )
        }
        val account = service.createExternalAccount(ExternalAccountDto(referenceId = "123", referenceName = "testref", type = "CHICKEN_SAUCE"))
        StepVerifier.create(account)
            .expectNext(
                ExternalAccountDto(
                    id = 9,
                    referenceId = "123",
                    referenceName = "testref",
                    type = "CHICKEN_SAUCE",
                    registrationTime = slot.captured.registrationTime,
                )
            )
            .verifyComplete()
    }
}
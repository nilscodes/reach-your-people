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
import java.time.OffsetDateTime
import java.util.Optional

internal class ExternalAccountsApiServiceVibrantTest {
    @Test
    fun `creating an external account works`() {
        val accountRepository = mockk<ExternalAccountRepository>()
        val service = ExternalAccountsApiServiceVibrant(accountRepository)
        val slot = slot<ExternalAccount>()
        every { accountRepository.save(capture(slot)) } answers {
            ExternalAccount(
                id = 9,
                referenceId = firstArg<ExternalAccount>().referenceId,
                referenceName = firstArg<ExternalAccount>().referenceName,
                displayName = firstArg<ExternalAccount>().displayName,
                type = firstArg<ExternalAccount>().type,
                registrationTime = firstArg<ExternalAccount>().registrationTime,
            )
        }
        val account = service.createExternalAccount(ExternalAccountDto(referenceId = "123", referenceName = "testref", displayName = "Tester McTestface", type = "CHICKEN_SAUCE"))
        StepVerifier.create(account)
            .expectNext(
                ExternalAccountDto(
                    id = 9,
                    referenceId = "123",
                    referenceName = "testref",
                    displayName = "Tester McTestface",
                    type = "CHICKEN_SAUCE",
                    registrationTime = slot.captured.registrationTime,
                )
            )
            .verifyComplete()
    }

    @Test
    fun `finding an external account by provider and reference ID works`() {
        val accountRepository = mockk<ExternalAccountRepository>()
        val service = ExternalAccountsApiServiceVibrant(accountRepository)
        val now = OffsetDateTime.now()
        val externalAccount = makeExternalAccount(1, now)
        every { accountRepository.findByTypeAndReferenceId("CHICKEN_SAUCE", "123") } returns Optional.of(externalAccount)
        val account = service.findExternalAccountByProviderAndReferenceId("CHICKEN_SAUCE", "123")
        StepVerifier.create(account)
            .expectNext(
                externalAccount.toDto()
            )
            .verifyComplete()
    }

    @Test
    fun `finding an external account by provider and reference ID fails correctly when not found`() {
        val accountRepository = mockk<ExternalAccountRepository>()
        val service = ExternalAccountsApiServiceVibrant(accountRepository)
        every { accountRepository.findByTypeAndReferenceId("CHICKEN_SAUCE", "123") } returns Optional.empty()
        val account = service.findExternalAccountByProviderAndReferenceId("CHICKEN_SAUCE", "123")
        StepVerifier.create(account)
            .expectError(NoSuchElementException::class.java)
            .verify()
    }

    private fun makeExternalAccount(id: Long, now: OffsetDateTime) = ExternalAccount(
        id = id,
        referenceId = "123",
        referenceName = "niu",
        displayName = "Ni U",
        registrationTime = now,
        type = "discord",
    )
}
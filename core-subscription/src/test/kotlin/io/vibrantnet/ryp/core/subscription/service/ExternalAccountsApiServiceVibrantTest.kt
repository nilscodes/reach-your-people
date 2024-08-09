package io.vibrantnet.ryp.core.subscription.service

import io.mockk.*
import io.ryp.shared.model.ExternalAccountDto
import io.vibrantnet.ryp.core.subscription.model.UnsubscribeFromEmailRequest
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccount
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.util.*

internal class ExternalAccountsApiServiceVibrantTest {

    private val externalAccountRepository = mockk<ExternalAccountRepository>()
    private val service = ExternalAccountsApiServiceVibrant(externalAccountRepository)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `creating an external account works`() {
        val slot = slot<ExternalAccount>()
        every { externalAccountRepository.save(capture(slot)) } answers {
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
    fun `creating an external account correctly decodes base64 into bytes for metadata if provided`() {
        val slot = slot<ExternalAccount>()
        every { externalAccountRepository.save(capture(slot)) } answers {
            ExternalAccount(
                id = 9,
                referenceId = firstArg<ExternalAccount>().referenceId,
                referenceName = firstArg<ExternalAccount>().referenceName,
                displayName = firstArg<ExternalAccount>().displayName,
                type = firstArg<ExternalAccount>().type,
                metadata = firstArg<ExternalAccount>().metadata,
                registrationTime = firstArg<ExternalAccount>().registrationTime,
            )
        }
        val account = service.createExternalAccount(ExternalAccountDto(referenceId = "123", referenceName = "testref", displayName = "Tester McTestface", type = "CHICKEN_SAUCE", metadata = "aGVsbG8="))
        StepVerifier.create(account)
            .expectNext(
                ExternalAccountDto(
                    id = 9,
                    referenceId = "123",
                    referenceName = "testref",
                    displayName = "Tester McTestface",
                    type = "CHICKEN_SAUCE",
                    registrationTime = slot.captured.registrationTime,
                    metadata = "aGVsbG8="
                )
            )
            .verifyComplete()
    }

    @Test
    fun `finding an external account by provider and reference ID works`() {
        val now = OffsetDateTime.now()
        val externalAccount = makeExternalAccount(1, now)
        every { externalAccountRepository.findByTypeAndReferenceId("CHICKEN_SAUCE", "123") } returns Optional.of(externalAccount)
        val account = service.findExternalAccountByProviderAndReferenceId("CHICKEN_SAUCE", "123")
        StepVerifier.create(account)
            .expectNext(
                externalAccount.toDto()
            )
            .verifyComplete()
    }

    @Test
    fun `finding an external account by provider and reference ID fails correctly when not found`() {
        every { externalAccountRepository.findByTypeAndReferenceId("CHICKEN_SAUCE", "123") } returns Optional.empty()
        val account = service.findExternalAccountByProviderAndReferenceId("CHICKEN_SAUCE", "123")
        StepVerifier.create(account)
            .expectError(NoSuchElementException::class.java)
            .verify()
    }

    @Test
    fun `unsubscribing from email works for both email and google providers`() {
        val now = OffsetDateTime.now()
        val externalMailAccountWithEmailAddress = makeExternalAccount(1, now)
        externalMailAccountWithEmailAddress.referenceId = "ryp@ryp.io"
        val googleMailAccountWithEmailAddress = makeExternalAccount(2, now)
        googleMailAccountWithEmailAddress.referenceName = "ryp@ryp.io"
        every { externalAccountRepository.findByTypeAndReferenceId("email", "ryp@ryp.io") } returns Optional.of(
            externalMailAccountWithEmailAddress
        )
        every { externalAccountRepository.findByTypeAndReferenceName("google", "ryp@ryp.io") } returns Optional.of(
            googleMailAccountWithEmailAddress
        )
        val unsubscribes = mutableListOf<ExternalAccount>()

        every { externalAccountRepository.save(capture(unsubscribes)) } returnsArgument 0

        val result = service.unsubscribeFromEmail(UnsubscribeFromEmailRequest("ryp@ryp.io"))
        StepVerifier.create(result)
            .verifyComplete()
        verify(exactly = 1) { externalAccountRepository.save(match { it.unsubscribeTime != null && it.referenceId == "ryp@ryp.io" })}
        verify(exactly = 1) { externalAccountRepository.save(match { it.unsubscribeTime != null && it.referenceName == "ryp@ryp.io" })}
    }

    @Test
    fun `unsubscribing does nothing and does not fail if no accounts found`() {
        every { externalAccountRepository.findByTypeAndReferenceId("email", any()) } returns Optional.empty()
        every { externalAccountRepository.findByTypeAndReferenceName("google", any()) } returns Optional.empty()
        val result = service.unsubscribeFromEmail(UnsubscribeFromEmailRequest("ryp@ryp.io"))
        StepVerifier.create(result)
            .verifyComplete()
        verify(exactly = 0) { externalAccountRepository.save(any()) }
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
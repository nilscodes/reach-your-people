package io.vibrantnet.ryp.core.subscription.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.ryp.shared.model.ExternalAccountDto
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccount
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.util.Optional

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

    private fun makeExternalAccount(id: Long, now: OffsetDateTime) = ExternalAccount(
        id = id,
        referenceId = "123",
        referenceName = "niu",
        displayName = "Ni U",
        registrationTime = now,
        type = "discord",
    )
}
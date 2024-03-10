package io.vibrantnet.ryp.core.subscription.service

import io.mockk.*
import io.vibrantnet.ryp.core.subscription.model.AccountDto
import io.vibrantnet.ryp.core.subscription.model.AccountPartialDto
import io.vibrantnet.ryp.core.subscription.model.ExternalAccountAlreadyLinkedException
import io.ryp.shared.model.LinkedExternalAccountDto
import io.vibrantnet.ryp.core.subscription.persistence.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.time.OffsetDateTime

internal class AccountsApiServiceVibrantTest {
    @Test
    fun `creating an account works`() {
        val accountRepository = mockk<AccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, mockk(), mockk())
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { Account(
            id = 12,
            displayName = firstArg<Account>().displayName,
            createTime = firstArg<Account>().createTime,
        )
        }
        val account = service.createAccount(AccountDto(displayName = "test"))
        StepVerifier.create(account)
            .expectNext(
                AccountDto(
                    id = 12,
                    displayName = "test",
                    createTime = slot.captured.createTime,
                )
            )
            .verifyComplete()
    }

    @Test
    fun `getting an account by id works`() {
        val accountRepository = mockk<AccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, mockk(), mockk())
        val now = OffsetDateTime.now()
        every { accountRepository.findById(12) } returns java.util.Optional.of(
            Account(
                id = 12,
                displayName = "test",
                createTime = now,
            )
        )
        val account = service.getAccountById(12)
        StepVerifier.create(account)
            .expectNext(
                AccountDto(
                    id = 12,
                    displayName = "test",
                    createTime = now,
                )
            )
            .verifyComplete()
    }

    @Test
    fun `correct exception thrown when getting account by ID fails`() {
        val accountRepository = mockk<AccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, mockk(), mockk())
        every { accountRepository.findById(12) } returns java.util.Optional.empty()
        assertThrows(NoSuchElementException::class.java) {
            service.getAccountById(12).block()
        }
    }

    @Test
    fun `getting an account by provider and reference ID of an external account works`() {
        val accountRepository = mockk<AccountRepository>()
        val externalAccountRepository = mockk<ExternalAccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, externalAccountRepository, mockk())
        val now = OffsetDateTime.now()
        every { externalAccountRepository.findByTypeAndReferenceId("discord", "123") } returns java.util.Optional.of(makeExternalAccount(1, now))
        every { accountRepository.findByLinkedExternalAccountsExternalAccountId(1) } returns listOf(makeAccount(now))
        val account = service.findAccountByProviderAndReferenceId("discord", "123")
        StepVerifier.create(account)
            .expectNext(
                AccountDto(
                    id = 12,
                    displayName = "test",
                    createTime = now,
                )
            )
            .verifyComplete()
    }

    @Test
    fun `getting an account by provider and reference ID throws an exception if no matching external account is found`() {
        val accountRepository = mockk<AccountRepository>()
        val externalAccountRepository = mockk<ExternalAccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, externalAccountRepository, mockk())
        every { externalAccountRepository.findByTypeAndReferenceId("discord", "123") } returns java.util.Optional.empty()
        assertThrows(NoSuchElementException::class.java) {
            service.findAccountByProviderAndReferenceId("discord", "123").block()
        }
    }

    @Test
    fun `getting an account by provider and reference ID throws an exception if no matching account is found`() {
        val accountRepository = mockk<AccountRepository>()
        val externalAccountRepository = mockk<ExternalAccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, externalAccountRepository, mockk())
        val now = OffsetDateTime.now()
        every { externalAccountRepository.findByTypeAndReferenceId("discord", "123") } returns java.util.Optional.of(makeExternalAccount(1, now))
        every { accountRepository.findByLinkedExternalAccountsExternalAccountId(1) } returns emptyList()
        assertThrows(NoSuchElementException::class.java) {
            service.findAccountByProviderAndReferenceId("discord", "123").block()
        }
    }

    @Test
    fun `getting an existing linked external accounts works`() {
        val accountRepository = mockk<AccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, mockk(), mockk())
        val now = OffsetDateTime.now()
        val account = makeAccount(now)
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        val linkedExternalAccounts = service.getLinkedExternalAccounts(12)
        StepVerifier.create(linkedExternalAccounts)
            .expectNext(
                LinkedExternalAccountDto(
                    externalAccount = account.linkedExternalAccounts.first().externalAccount.toDto(),
                    role = LinkedExternalAccountDto.ExternalAccountRole.OWNER,
                    linkTime = now,
                )
            ).verifyComplete()
    }

    @Test
    fun `linking an external account works`() {
        val accountRepository = mockk<AccountRepository>()
        val externalAccountRepository = mockk<ExternalAccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, externalAccountRepository, mockk())
        val account = makeAccount(OffsetDateTime.now())
        val externalAccount = makeExternalAccount(2, OffsetDateTime.now())
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        every { externalAccountRepository.findById(2) } returns java.util.Optional.of(externalAccount)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        val linkedExternalAccount = service.linkExternalAccount(2, 12)
        StepVerifier.create(linkedExternalAccount)
            .expectNext(
                LinkedExternalAccountDto(
                    externalAccount = externalAccount.toDto(),
                    role = LinkedExternalAccountDto.ExternalAccountRole.OWNER,
                    linkTime = slot.captured.linkedExternalAccounts.last().linkTime,
                )
            ).verifyComplete()
    }

    @Test
    fun `linking an external account that already exists throws the appropriate exception`() {
        val accountRepository = mockk<AccountRepository>()
        val externalAccountRepository = mockk<ExternalAccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, externalAccountRepository, mockk())
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        every { externalAccountRepository.findById(1) } returns java.util.Optional.of(makeExternalAccount(1, OffsetDateTime.now()))
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        assertThrows(ExternalAccountAlreadyLinkedException::class.java) {
            service.linkExternalAccount(1, 12).block()
        }
    }

    @Test
    fun `unlinking an external account works and does not delete the external account if not the last link`() {
        val accountRepository = mockk<AccountRepository>()
        val externalAccountRepository = mockk<ExternalAccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, externalAccountRepository, mockk())
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        every { accountRepository.findByLinkedExternalAccountsExternalAccountId(1) } returns listOf(makeAccount(OffsetDateTime.now(), 13))
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        service.unlinkExternalAccount(12, 1)
        assertTrue(slot.captured.linkedExternalAccounts.none { it.externalAccount.id == 1L })
        verify(exactly = 0) { externalAccountRepository.deleteById(any()) }
    }

    @Test
    fun `unlinking an external account works and does delete the external account if it was the last link`() {
        val accountRepository = mockk<AccountRepository>()
        val externalAccountRepository = mockk<ExternalAccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, externalAccountRepository, mockk())
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        every { accountRepository.findByLinkedExternalAccountsExternalAccountId(1) } returns emptyList()
        every { externalAccountRepository.deleteById(1) } just Runs
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        service.unlinkExternalAccount(12, 1)
        assertTrue(slot.captured.linkedExternalAccounts.none { it.externalAccount.id == 1L })
        verify { externalAccountRepository.deleteById(1) }
    }

    @Test
    fun `unlinking a non-existing external accounts throws an appropriate exception`() {
        val accountRepository = mockk<AccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, mockk(), mockk())
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        assertThrows(NoSuchElementException::class.java) {
            service.unlinkExternalAccount(12, 2)
        }
    }

    @Test
    fun `updating an account with changes saves`() {
        val accountRepository = mockk<AccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, mockk(), mockk())
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        val updatedAccount = service.updateAccountById(12, AccountPartialDto(displayName = "new name"))
        assertEquals("new name", slot.captured.displayName)
        StepVerifier.create(updatedAccount)
            .expectNext(
                AccountDto(
                    id = 12,
                    displayName = "new name",
                    createTime = account.createTime,
                )
            )
            .verifyComplete()
    }

    @Test
    fun `updating an account with no effective changes is a no-op`() {
        val accountRepository = mockk<AccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, mockk(), mockk())
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        val updatedAccount = service.updateAccountById(12, AccountPartialDto(displayName = "test"))
        verify(exactly = 0) { accountRepository.save(any()) }
        StepVerifier.create(updatedAccount)
            .expectNext(
                AccountDto(
                    id = 12,
                    displayName = "test",
                    createTime = account.createTime,
                )
            )
            .verifyComplete()
    }

    @Test
    fun `updating an account without providing any change data is a no-op`() {
        val accountRepository = mockk<AccountRepository>()
        val service = AccountsApiServiceVibrant(accountRepository, mockk(), mockk())
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        val updatedAccount = service.updateAccountById(12, AccountPartialDto())
        verify(exactly = 0) { accountRepository.save(any()) }
        StepVerifier.create(updatedAccount)
            .expectNext(
                AccountDto(
                    id = 12,
                    displayName = "test",
                    createTime = account.createTime,
                )
            )
            .verifyComplete()
    }

    private fun makeAccount(now: OffsetDateTime, id: Long = 12) = Account(
        id = id,
        displayName = "test",
        createTime = now,
        linkedExternalAccounts = mutableSetOf(
            LinkedExternalAccount(
                externalAccount = makeExternalAccount(1, now),
                role = LinkedExternalAccountDto.ExternalAccountRole.OWNER,
                linkTime = now,
            ),
        )
    )

    private fun makeExternalAccount(id: Long, now: OffsetDateTime) = ExternalAccount(
        id = id,
        referenceId = "123",
        referenceName = "niu",
        displayName = "Ni U",
        registrationTime = now,
        type = "discord",
    )
}
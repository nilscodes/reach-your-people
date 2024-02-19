package io.vibrantnet.ryp.core.subscription.service

import io.mockk.*
import io.vibrantnet.ryp.core.subscription.model.AccountDto
import io.vibrantnet.ryp.core.subscription.model.AccountPartialDto
import io.vibrantnet.ryp.core.subscription.model.ExternalAccountAlreadyLinkedException
import io.vibrantnet.ryp.core.subscription.model.LinkedExternalAccountDto
import io.vibrantnet.ryp.core.subscription.persistence.Account
import io.vibrantnet.ryp.core.subscription.persistence.AccountRepository
import io.vibrantnet.ryp.core.subscription.persistence.LinkedExternalAccount
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.time.OffsetDateTime

internal class AccountsApiServiceVibrantTest {
    @Test
    fun `creating an account works`() {
        val accountRepository = mockk<AccountRepository>();
        val service = AccountsApiServiceVibrant(accountRepository)
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
        val accountRepository = mockk<AccountRepository>();
        val service = AccountsApiServiceVibrant(accountRepository)
        every { accountRepository.findById(12) } returns java.util.Optional.of(
            Account(
                id = 12,
                displayName = "test",
                createTime = OffsetDateTime.now(),
            )
        )
        val account = service.getAccountById(12)
        StepVerifier.create(account)
            .expectNext(
                AccountDto(
                    id = 12,
                    displayName = "test",
                    createTime = account.block()?.createTime,
                )
            )
            .verifyComplete()
    }

    @Test
    fun `correct exception thrown when getting account by ID fails`() {
        val accountRepository = mockk<AccountRepository>();
        val service = AccountsApiServiceVibrant(accountRepository)
        every { accountRepository.findById(12) } returns java.util.Optional.empty()
        assertThrows(NoSuchElementException::class.java) {
            service.getAccountById(12).block()
        }
    }

    @Test
    fun `getting an existing linked external accounts works`() {
        val accountRepository = mockk<AccountRepository>();
        val service = AccountsApiServiceVibrant(accountRepository)
        val now = OffsetDateTime.now()
        every { accountRepository.findById(12) } returns java.util.Optional.of(
            makeAccount(now)
        )
        val linkedExternalAccounts = service.getLinkedExternalAccounts(12)
        StepVerifier.create(linkedExternalAccounts)
            .expectNext(
                LinkedExternalAccountDto(
                    externalAccountId = 1,
                    role = LinkedExternalAccountDto.ExternalAccountRole.OWNER,
                    linkTime = now,
                )
            ).verifyComplete()
    }

    @Test
    fun `linking an external account works`() {
        val accountRepository = mockk<AccountRepository>();
        val service = AccountsApiServiceVibrant(accountRepository)
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        val linkedExternalAccount = service.linkExternalAccount(2, 12)
        StepVerifier.create(linkedExternalAccount)
            .expectNext(
                LinkedExternalAccountDto(
                    externalAccountId = 2,
                    role = LinkedExternalAccountDto.ExternalAccountRole.OWNER,
                    linkTime = slot.captured.linkedExternalAccounts.last().linkTime,
                )
            ).verifyComplete()
    }

    @Test
    fun `linking an external account that already exists throws the appropriate exception`() {
        val accountRepository = mockk<AccountRepository>();
        val service = AccountsApiServiceVibrant(accountRepository)
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        assertThrows(ExternalAccountAlreadyLinkedException::class.java) {
            service.linkExternalAccount(1, 12).block()
        }
    }

    @Test
    fun `unlinking an external account works`() {
        val accountRepository = mockk<AccountRepository>();
        val service = AccountsApiServiceVibrant(accountRepository)
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns java.util.Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        service.unlinkExternalAccount(12, 1)
        assertTrue(slot.captured.linkedExternalAccounts.none { it.externalAccountId == 1L })
    }

    @Test
    fun `unlinking a non-existing external accounts throws an appropriate exception`() {
        val accountRepository = mockk<AccountRepository>();
        val service = AccountsApiServiceVibrant(accountRepository)
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
        val accountRepository = mockk<AccountRepository>();
        val service = AccountsApiServiceVibrant(accountRepository)
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
        val accountRepository = mockk<AccountRepository>();
        val service = AccountsApiServiceVibrant(accountRepository)
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
        val accountRepository = mockk<AccountRepository>();
        val service = AccountsApiServiceVibrant(accountRepository)
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

    private fun makeAccount(now: OffsetDateTime) = Account(
        id = 12,
        displayName = "test",
        createTime = now,
        linkedExternalAccounts = mutableSetOf(
            LinkedExternalAccount(
                externalAccountId = 1,
                role = LinkedExternalAccountDto.ExternalAccountRole.OWNER,
                linkTime = now,
            ),
        )
    )
}
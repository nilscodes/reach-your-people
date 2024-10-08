package io.vibrantnet.ryp.core.subscription.service

import io.mockk.*
import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import io.ryp.cardano.model.governance.DRepDetailsDto
import io.ryp.cardano.model.stakepools.StakepoolDetailsDto
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.subscription.model.*
import io.vibrantnet.ryp.core.subscription.persistence.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*

internal class AccountsApiServiceVibrantTest {

    private val accountRepository = mockk<AccountRepository>()
    private val externalAccountRepository = mockk<ExternalAccountRepository>()
    private val linkedExternalAccountRepository = mockk<LinkedExternalAccountRepository>()
    private val projectRepository = mockk<ProjectRepository>()
    private val verifyService = mockk<VerifyService>()
    private val projectNotificationSettingRepository = mockk<ProjectNotificationSettingRepository>()
    private val service = AccountsApiServiceVibrant(
        accountRepository,
        externalAccountRepository,
        linkedExternalAccountRepository,
        projectRepository,
        verifyService,
        projectNotificationSettingRepository
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `creating an account works`() {
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers {
            Account(
                id = 12,
                displayName = firstArg<Account>().displayName,
                createTime = firstArg<Account>().createTime,
            )
        }
        val account = service.createAccount(AccountDto(displayName = "test"), 13)
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
        val now = OffsetDateTime.now()
        every { accountRepository.findById(12) } returns Optional.of(
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
        every { accountRepository.findById(12) } returns Optional.empty()
        assertThrows(NoSuchElementException::class.java) {
            service.getAccountById(12).block()
        }
    }

    @Test
    fun `getting an account by provider and reference ID of an external account works`() {
        val now = OffsetDateTime.now()
        every { externalAccountRepository.findByTypeAndReferenceId("discord", "123") } returns Optional.of(
            makeExternalAccount(1, now)
        )
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
        every {
            externalAccountRepository.findByTypeAndReferenceId(
                "discord",
                "123"
            )
        } returns Optional.empty()
        assertThrows(NoSuchElementException::class.java) {
            service.findAccountByProviderAndReferenceId("discord", "123").block()
        }
    }

    @Test
    fun `getting an account by provider and reference ID throws an exception if no matching account is found`() {
        val now = OffsetDateTime.now()
        every { externalAccountRepository.findByTypeAndReferenceId("discord", "123") } returns Optional.of(
            makeExternalAccount(1, now)
        )
        every { accountRepository.findByLinkedExternalAccountsExternalAccountId(1) } returns emptyList()
        assertThrows(NoSuchElementException::class.java) {
            service.findAccountByProviderAndReferenceId("discord", "123").block()
        }
    }

    @Test
    fun `getting an existing linked external accounts works`() {
        val now = OffsetDateTime.now()
        val account = makeAccount(now)
        every { accountRepository.findById(12) } returns Optional.of(account)
        val linkedExternalAccounts = service.getLinkedExternalAccounts(12)
        StepVerifier.create(linkedExternalAccounts)
            .expectNext(
                LinkedExternalAccountDto(
                    id = account.linkedExternalAccounts.first().id!!,
                    externalAccount = account.linkedExternalAccounts.first().externalAccount.toDto(),
                    role = ExternalAccountRole.OWNER,
                    linkTime = now,
                )
            ).verifyComplete()
    }

    @Test
    fun `linking an external account works`() {
        val account = makeAccount(OffsetDateTime.now())
        val externalAccount = makeExternalAccount(2, OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
        every { externalAccountRepository.findById(2) } returns Optional.of(externalAccount)
        val slot = slot<LinkedExternalAccount>()
        // Returns called argument with the ID set
        every { linkedExternalAccountRepository.save(capture(slot)) } answers {
            LinkedExternalAccount(
                id = 14909,
                accountId = firstArg<LinkedExternalAccount>().accountId,
                externalAccount = firstArg<LinkedExternalAccount>().externalAccount,
                role = firstArg<LinkedExternalAccount>().role,
                linkTime = firstArg<LinkedExternalAccount>().linkTime,
                lastConfirmed = firstArg<LinkedExternalAccount>().lastConfirmed,
            )
        }
        every { linkedExternalAccountRepository.updateSettings(14909, any()) } just Runs
        val linkedExternalAccount = service.linkExternalAccount(2, 12)
        StepVerifier.create(linkedExternalAccount)
            .assertNext {
                assertEquals(it.id, 14909)
                assertEquals(it.externalAccount, slot.captured.externalAccount.toDto())
                assertEquals(it.role, slot.captured.role)
                assertEquals(it.linkTime, slot.captured.linkTime)
                assertNotNull(slot.captured.lastConfirmed)
            }.verifyComplete()
    }

    @Test
    fun `linking an external account that already exists throws the appropriate exception`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
        every { externalAccountRepository.findById(1) } returns Optional.of(
            makeExternalAccount(
                1,
                OffsetDateTime.now()
            )
        )
        every { linkedExternalAccountRepository.save(any()) } throws DataIntegrityViolationException("bad unique constraint violation")
        assertThrows(ExternalAccountAlreadyLinkedException::class.java) {
            service.linkExternalAccount(1, 12).block()
        }
    }

    @Test
    fun `unlinking an external account works and does not delete the external account if not the last link`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
        every { accountRepository.findByLinkedExternalAccountsExternalAccountId(1) } returns listOf(
            makeAccount(
                OffsetDateTime.now(),
                13
            )
        )
        every { linkedExternalAccountRepository.deleteDirectly(any()) } just Runs
        service.unlinkExternalAccount(12, 1)
        verify(exactly = 1) { linkedExternalAccountRepository.deleteDirectly(account.linkedExternalAccounts.first().id!!) }
        verify(exactly = 0) { externalAccountRepository.deleteById(any()) }
    }

    @Test
    fun `unlinking an external account works and does delete the external account if it was the last link`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
        every { accountRepository.findByLinkedExternalAccountsExternalAccountId(1) } returns emptyList()
        every { externalAccountRepository.deleteById(1) } just Runs
        every { linkedExternalAccountRepository.deleteDirectly(any()) } just Runs
        service.unlinkExternalAccount(12, 1)
        verify(exactly = 1) { linkedExternalAccountRepository.deleteDirectly(account.linkedExternalAccounts.first().id!!) }
        verify { externalAccountRepository.deleteById(1) }
    }

    @Test
    fun `unlinking a non-existing external accounts throws an appropriate exception`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        assertThrows(NoSuchElementException::class.java) {
            service.unlinkExternalAccount(12, 2)
        }
    }

    @Test
    fun `updating an account with changes saves`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
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
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
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
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
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

    @Test
    fun `updating an account that is missing throws an error`() {
        every { accountRepository.findById(12) } returns Optional.empty()
        val result = service.updateAccountById(12, AccountPartialDto(displayName = "test"))
        StepVerifier.create(result)
            .verifyError(NoSuchElementException::class.java)
    }

    @Test
    fun `updating Cardano settings of an account triggers no standard save but a custom SQL call`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
        every { accountRepository.updateCardanoSettings(any(), any()) } just Runs
        val updatedAccount = service.updateAccountById(12, AccountPartialDto(cardanoSettings = setOf(CardanoSetting.GOVERNANCE_ACTION_ANNOUNCEMENTS)))
        verify(exactly = 0) { accountRepository.save(any()) }
        verify(exactly = 1) { accountRepository.updateCardanoSettings(12, "1111111111111111") }
        StepVerifier.create(updatedAccount)
            .expectNext(
                AccountDto(
                    id = 12,
                    displayName = "test",
                    createTime = account.createTime,
                    cardanoSettings = setOf(CardanoSetting.GOVERNANCE_ACTION_ANNOUNCEMENTS),
                )
            )
            .verifyComplete()
    }

    @Test
    fun `updating premium time when the existing premium time is in the past pushes the date out from now instead of the expired timestamp`() {
        val account = makeAccount(OffsetDateTime.now())
        account.premiumUntil = OffsetDateTime.now().minusMonths(1)
        every { accountRepository.findById(12) } returns Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        val updatedAccount = service.extendPremium(12, Duration.of(1, ChronoUnit.DAYS))
        StepVerifier.create(updatedAccount)
            .expectNextMatches {
                it.premiumUntil!!.isAfter(OffsetDateTime.now())
            }
            .verifyComplete()
    }

    @Test
    fun `updating premium time when the existing premium time is in the future pushes it out from that date instead of now`() {
        val account = makeAccount(OffsetDateTime.now())
        account.premiumUntil = OffsetDateTime.now().plusMonths(1)
        every { accountRepository.findById(12) } returns Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        val updatedAccount = service.extendPremium(12, Duration.of(90, ChronoUnit.DAYS))
        StepVerifier.create(updatedAccount)
            .expectNextMatches {
                it.premiumUntil!!.isAfter(OffsetDateTime.now().plusMonths(2))
            }
            .verifyComplete()
    }

    @Test
    fun `default settings are correct for cardano external account`() {
        val settings = service.getDefaultSettingsForExternalAccount(
            ExternalAccount(
                id = 1,
                referenceId = "123",
                referenceName = "niu",
                displayName = "Ni U",
                registrationTime = OffsetDateTime.now(),
                type = "cardano",
            )
        )
        assertEquals(
            setOf(
                ExternalAccountSetting.NON_FUNGIBLE_TOKEN_ANNOUNCEMENTS,
                ExternalAccountSetting.FUNGIBLE_TOKEN_ANNOUNCEMENTS,
                ExternalAccountSetting.RICH_FUNGIBLE_TOKEN_ANNOUNCEMENTS,
                ExternalAccountSetting.STAKEPOOL_ANNOUNCEMENTS,
                ExternalAccountSetting.DREP_ANNOUNCEMENTS,
            ), settings
        )
    }

    @Test
    fun `default settings are correct for discord external account`() {
        val settings = service.getDefaultSettingsForExternalAccount(
            ExternalAccount(
                id = 1,
                referenceId = "123",
                referenceName = "niu",
                displayName = "Ni U",
                registrationTime = OffsetDateTime.now(),
                type = "discord",
            )
        )
        assertTrue(settings.isEmpty())
    }

    @Test
    fun `updating linked external account with only settings works`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
        val linkedExternalAccount = account.linkedExternalAccounts.first()
        val slot = slot<LinkedExternalAccount>()
        every { linkedExternalAccountRepository.save(capture(slot)) } answers { slot.captured }
        every { linkedExternalAccountRepository.updateSettings(any(), any()) } just Runs
        val updatedLinkedExternalAccount = service.updateLinkedExternalAccount(12, 1, LinkedExternalAccountPartialDto(
            settings = setOf(ExternalAccountSetting.DREP_ANNOUNCEMENTS)
        ))
        assertEquals("1111111111010000", slot.captured.settings)
        StepVerifier.create(updatedLinkedExternalAccount)
            .expectNext(
                LinkedExternalAccountDto(
                    id = 877,
                    externalAccount = linkedExternalAccount.externalAccount.toDto(),
                    role = ExternalAccountRole.OWNER,
                    linkTime = linkedExternalAccount.linkTime,
                    settings = setOf(ExternalAccountSetting.DREP_ANNOUNCEMENTS),
                )
            )
            .verifyComplete()

        verify {
            linkedExternalAccountRepository.updateSettings(877, "1111111111010000")
        }
    }

    @Test
    fun `updating linked external account with only lastConfirmed works`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
        val linkedExternalAccount = account.linkedExternalAccounts.first()
        val slot = slot<LinkedExternalAccount>()
        every { linkedExternalAccountRepository.save(capture(slot)) } answers { slot.captured }
        every { linkedExternalAccountRepository.updateSettings(any(), any()) } just Runs
        val updatedLinkedExternalAccount = service.updateLinkedExternalAccount(12, 1, LinkedExternalAccountPartialDto(
            lastConfirmed = OffsetDateTime.now()
        ))
        assertNotNull(slot.captured.lastConfirmed)
        StepVerifier.create(updatedLinkedExternalAccount)
            .expectNext(
                LinkedExternalAccountDto(
                    id = 877,
                    externalAccount = linkedExternalAccount.externalAccount.toDto(),
                    role = ExternalAccountRole.OWNER,
                    linkTime = linkedExternalAccount.linkTime,
                    lastConfirmed = slot.captured.lastConfirmed,
                )
            )
            .verifyComplete()
    }

    @Test
    fun `updating linked external account with only lastTested works`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
        val linkedExternalAccount = account.linkedExternalAccounts.first()
        val slot = slot<LinkedExternalAccount>()
        every { linkedExternalAccountRepository.save(capture(slot)) } answers { slot.captured }
        every { linkedExternalAccountRepository.updateSettings(any(), any()) } just Runs
        val updatedLinkedExternalAccount = service.updateLinkedExternalAccount(12, 1, LinkedExternalAccountPartialDto(
            lastTested = OffsetDateTime.now()
        ))
        assertNotNull(slot.captured.lastTested)
        StepVerifier.create(updatedLinkedExternalAccount)
            .expectNext(
                LinkedExternalAccountDto(
                    id = 877,
                    externalAccount = linkedExternalAccount.externalAccount.toDto(),
                    role = ExternalAccountRole.OWNER,
                    linkTime = linkedExternalAccount.linkTime,
                    lastTested = slot.captured.lastTested,
                )
            )
            .verifyComplete()
    }

    @Test
    fun `cannot update a linked external account if the account is not an owner of the external account`() {
        val account = makeAccount(OffsetDateTime.now(), 12, ExternalAccountRole.ADMIN)
        every { accountRepository.findById(12) } returns Optional.of(account)

        val result = service.updateLinkedExternalAccount(12, 1, LinkedExternalAccountPartialDto(
            settings = setOf(ExternalAccountSetting.DREP_ANNOUNCEMENTS)
        ))

        StepVerifier.create(result)
            .verifyError(PermissionDeniedException::class.java)
    }

    @Test
    fun `cannot update linked external account if not associated with the presented account`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)

        val result = service.updateLinkedExternalAccount(12, 2, LinkedExternalAccountPartialDto(
            settings = setOf(ExternalAccountSetting.DREP_ANNOUNCEMENTS)
        ))

        StepVerifier.create(result)
            .verifyError(NoSuchElementException::class.java)
    }

    @Test
    fun `subscribing account to project works`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
        every { projectRepository.findById(1) } returns Optional.of(makeProject(1))
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        val updatedAccount = service.subscribeAccountToProject(12, 1, NewSubscriptionDto(status = SubscriptionStatus.SUBSCRIBED))
        assertTrue(slot.captured.subscriptions.contains(Subscription(1, SubscriptionStatus.SUBSCRIBED)))
        StepVerifier.create(updatedAccount)
            .expectNext(NewSubscriptionDto(status = SubscriptionStatus.SUBSCRIBED))
            .verifyComplete()
    }

    @Test
    fun `subscribing with account that does not exist returns an error`() {
        every { accountRepository.findById(12) } returns Optional.empty()
        every { projectRepository.findById(1) } returns Optional.of(makeProject(1))
        val result = service.subscribeAccountToProject(12, 1, NewSubscriptionDto(status = SubscriptionStatus.SUBSCRIBED))
        StepVerifier.create(result)
            .verifyError(NoSuchElementException::class.java)
    }

    @Test
    fun `subscribing to a project that does not exist returns an error`() {
        every { accountRepository.findById(12) } returns Optional.of(makeAccount(OffsetDateTime.now()))
        every { projectRepository.findById(1) } returns Optional.empty()
        val result = service.subscribeAccountToProject(12, 1, NewSubscriptionDto(status = SubscriptionStatus.SUBSCRIBED))
        StepVerifier.create(result)
            .verifyError(NoSuchElementException::class.java)
    }

    @Test
    fun `unsubscribing from a project works if already subscribed`() {
        val account = makeAccount(OffsetDateTime.now())
        account.subscriptions.add(Subscription(1, SubscriptionStatus.SUBSCRIBED))
        every { accountRepository.findById(12) } returns Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        val updatedAccount = service.unsubscribeAccountFromProject(12, 1)
        assertTrue(slot.captured.subscriptions.none { it.projectId == 1L })
        StepVerifier.create(updatedAccount)
            .verifyComplete()
    }

    @Test
    fun `unsubscribing from a project with an account that does not exist returns an error`() {
        every { accountRepository.findById(12) } returns Optional.empty()
        val result = service.unsubscribeAccountFromProject(12, 1)
        StepVerifier.create(result)
            .verifyError(NoSuchElementException::class.java)
    }

    @Test
    fun `getting all subscriptions of an account works and merges settings correctly if only token policies are present`() {
        val account = prepareSubscribedTestAccount()
        every { verifyService.getPoliciesInWallet("stake1herpderp") } answers {
            Flux.fromIterable(
                listOf(
                    TokenOwnershipInfoWithAssetCount("stake1herpderp", "policy1", 1),
                    TokenOwnershipInfoWithAssetCount("stake1herpderp", "policy2", 2),
                )
            )
        }
        every { verifyService.getStakepoolDetailsForStakeAddress("stake1herpderp") } answers { Mono.empty() }
        every { verifyService.getDRepDetailsForStakeAddress("stake1herpderp") } answers { Mono.empty() }

        every { projectRepository.findByPoliciesPolicyIdIn(listOf("policy1", "policy2")) } returns listOf(makeProject(1), makeProject(2))
        every { accountRepository.findById(12) } returns Optional.of(account)
        val subscriptions = service.getAllSubscriptionsForAccount(12)
        verifyRetrievedSubscriptions(subscriptions)
    }

    @Test
    fun `getting all subscriptions of an account works and merges settings correctly if only stake pools are present`() {
        val account = prepareSubscribedTestAccount()
        every { verifyService.getPoliciesInWallet("stake1herpderp") } answers { Flux.empty() }
        every { verifyService.getStakepoolDetailsForStakeAddress("stake1herpderp") } answers { Mono.just(
            StakepoolDetailsDto("poolHash", "poolName", "poolTicker", "poolUrl", "poolDescription")
        ) }
        every { verifyService.getDRepDetailsForStakeAddress("stake1herpderp") } answers { Mono.empty() }

        every { projectRepository.findByStakepoolsPoolHashIn(listOf("poolHash")) } returns listOf(makeProject(1), makeProject(2))
        every { accountRepository.findById(12) } returns Optional.of(account)
        val subscriptions = service.getAllSubscriptionsForAccount(12)
        verifyRetrievedSubscriptions(subscriptions)
    }

    @Test
    fun `getting all subscriptions of an account works and merges settings correctly if only dReps are present`() {
        val account = prepareSubscribedTestAccount()
        every { verifyService.getPoliciesInWallet("stake1herpderp") } answers { Flux.empty() }
        every { verifyService.getStakepoolDetailsForStakeAddress("stake1herpderp") } answers { Mono.empty() }
        every { verifyService.getDRepDetailsForStakeAddress("stake1herpderp") } answers {
            Mono.just(DRepDetailsDto("drepId", "drepView", "Drep Guy", 12, 5000, 16))
        }

        every { projectRepository.findByDrepsDrepIdIn(listOf("drepId")) } returns listOf(makeProject(1), makeProject(2))
        every { accountRepository.findById(12) } returns Optional.of(account)
        val subscriptions = service.getAllSubscriptionsForAccount(12)
        verifyRetrievedSubscriptions(subscriptions)
    }

    private fun prepareSubscribedTestAccount(): Account {
        val account = makeAccount(OffsetDateTime.now())
        account.subscriptions.add(
            Subscription(
                1,
                SubscriptionStatus.BLOCKED
            )
        ) // The explicit unsubscribe will override the wallet-based subscription
        account.subscriptions.add(
            Subscription(
                3,
                SubscriptionStatus.SUBSCRIBED
            )
        ) // This one will be kept because there is no wallet-based override
        account.linkedExternalAccounts.add(
            LinkedExternalAccount(
                id = 879,
                accountId = 12,
                externalAccount = ExternalAccount(
                    id = 1,
                    referenceId = "stake1herpderp",
                    referenceName = "stake1",
                    displayName = "stake1",
                    registrationTime = OffsetDateTime.now(),
                    type = "cardano",
                ),
                role = ExternalAccountRole.OWNER,
                linkTime = OffsetDateTime.now(),
            )
        )
        return account
    }


    private fun verifyRetrievedSubscriptions(subscriptions: Flux<ProjectSubscriptionDto>) {
        StepVerifier.create(subscriptions.collectList())
            .assertNext {
                it.containsAll(
                    listOf(
                        ProjectSubscriptionDto(
                            projectId = 1,
                            defaultStatus = DefaultSubscriptionStatus.SUBSCRIBED,
                            currentStatus = SubscriptionStatus.BLOCKED,
                        ),
                        ProjectSubscriptionDto(
                            projectId = 2,
                            defaultStatus = DefaultSubscriptionStatus.SUBSCRIBED,
                            currentStatus = SubscriptionStatus.DEFAULT,
                        ),
                        ProjectSubscriptionDto(
                            projectId = 3,
                            defaultStatus = DefaultSubscriptionStatus.UNSUBSCRIBED,
                            currentStatus = SubscriptionStatus.SUBSCRIBED,
                        )
                    )
                )
            }
            .verifyComplete()
    }

    @Test
    fun `getting settings for an account works`() {
        val account = makeAccount(OffsetDateTime.now())
        account.settings = mutableSetOf(EmbeddableSetting("TEST", "test"), EmbeddableSetting("ROLLTOP", "false"))
        every { accountRepository.findById(12) } returns Optional.of(account)
        val settings = service.getSettingsForAccount(12)
        StepVerifier.create(settings)
            .expectNext(
                SettingsDto(
                    setOf(
                        SettingDto("TEST", "test"),
                        SettingDto("ROLLTOP", "false"),
                    )
                )
            )
            .verifyComplete()
    }

    @Test
    fun `updating account settings works if the setting is not already set`() {
        val account = makeAccount(OffsetDateTime.now())
        every { accountRepository.findById(12) } returns Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        val updatedSettings = service.updateAccountSetting(12, "TEST", SettingDto("TEST", "test"))
        StepVerifier.create(updatedSettings)
            .expectNext(
                SettingDto("TEST", "test")
            )
            .verifyComplete()
        assertTrue(slot.captured.settings.any { it.name == "TEST" && it.value == "test" })
    }

    @Test
    fun `updating account settings work if the setting is already set`() {
        val account = makeAccount(OffsetDateTime.now())
        account.settings = mutableSetOf(EmbeddableSetting("TEST", "test"))
        every { accountRepository.findById(12) } returns Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        val updatedSettings = service.updateAccountSetting(12, "TEST", SettingDto("TEST", "new value"))
        StepVerifier.create(updatedSettings)
            .expectNext(
                SettingDto("TEST", "new value")
            )
            .verifyComplete()
        assertTrue(slot.captured.settings.any { it.name == "TEST" && it.value == "new value" })
    }

    @Test
    fun `deleting account settings works`() {
        val account = makeAccount(OffsetDateTime.now())
        account.settings = mutableSetOf(EmbeddableSetting("TEST", "test"))
        every { accountRepository.findById(12) } returns Optional.of(account)
        val slot = slot<Account>()
        every { accountRepository.save(capture(slot)) } answers { slot.captured }
        val updatedSettings = service.deleteAccountSetting(12, "TEST")
        StepVerifier.create(updatedSettings)
            .verifyComplete()
        assertTrue(slot.captured.settings.none { it.name == "TEST" })
    }

    @Test
    fun `get notification settings for a specific project works`() {
        val notificationSetting = ProjectNotificationSetting(
            id = 601,
            projectId = 1,
            makeLinkedExternalAccount(12, OffsetDateTime.now(), ExternalAccountRole.OWNER),
            createTime = OffsetDateTime.now(),
        )
        every { projectNotificationSettingRepository.findByAccountIdAndProjectId(12, 1) } returns listOf(notificationSetting)
        val settings = service.getNotificationsSettingsForAccountAndProject(12, 1)
        StepVerifier.create(settings)
            .expectNext(notificationSetting.toDto())
            .verifyComplete()

    }

    @Test
    fun `update notification settings for a specific project works and removes and adds notifications as needed`() {
        val account = makeAccount(OffsetDateTime.now())
        val project = makeProject(1)
        val existingNotificationSetting1 = ProjectNotificationSetting(
            id = 601,
            projectId = 1,
            makeLinkedExternalAccount(12, OffsetDateTime.now(), ExternalAccountRole.OWNER, 91),
            createTime = OffsetDateTime.now(),
        )
        val existingNotificationSetting2 = ProjectNotificationSetting(
            id = 551,
            projectId = 2,
            makeLinkedExternalAccount(12, OffsetDateTime.now(), ExternalAccountRole.OWNER, 576),
            createTime = OffsetDateTime.now(),
        )
        every { accountRepository.findById(12) } returns Optional.of(account)
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectNotificationSettingRepository.findByAccountIdAndProjectId(12, 1) } returns listOf(existingNotificationSetting1, existingNotificationSetting2)
        every { projectNotificationSettingRepository.save(any()) } answers { firstArg<ProjectNotificationSetting>() }
        every { projectNotificationSettingRepository.delete(any()) } just Runs
        every { linkedExternalAccountRepository.findById(2000) } returns Optional.of(makeLinkedExternalAccount(12, OffsetDateTime.now(), ExternalAccountRole.OWNER, 2000))
        val updatedSettings = service.updateNotificationsSettingsForAccountAndProject(12, 1, listOf(ProjectNotificationSettingDto(
            projectId = 1,
            externalAccountLinkId = 2000,
        ), ProjectNotificationSettingDto(
            projectId = 2,
            externalAccountLinkId = 576,
        )))

        StepVerifier.create(updatedSettings.collectList())
            .assertNext {
                it.any { notification -> notification.externalAccountLinkId == 21L && notification.projectId == 1L }
                && it.any { notification -> notification.externalAccountLinkId == 551L && notification.projectId == 2L }
                && it.none { notification -> notification.externalAccountLinkId == 601L && notification.projectId == 1L }
            }
            .verifyComplete()

        verify(exactly = 1) {
            projectNotificationSettingRepository.delete(existingNotificationSetting1)
        }
    }

    @Test
    fun `cannot set notifications for a linked external account you are not owner of`() {
        val account = makeAccount(OffsetDateTime.now(), 12, ExternalAccountRole.ADMIN)
        val project = makeProject(1)
        val existingNotificationSetting1 = ProjectNotificationSetting(
            id = 601,
            projectId = 1,
            makeLinkedExternalAccount(12, OffsetDateTime.now(), ExternalAccountRole.OWNER, 91),
            createTime = OffsetDateTime.now(),
        )
        every { accountRepository.findById(12) } returns Optional.of(account)
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectNotificationSettingRepository.findByAccountIdAndProjectId(12, 1) } returns listOf(existingNotificationSetting1)
        every { projectNotificationSettingRepository.save(any()) } answers { firstArg<ProjectNotificationSetting>() }
        every { projectNotificationSettingRepository.delete(any()) } just Runs
        every { linkedExternalAccountRepository.findById(2000) } returns Optional.of(makeLinkedExternalAccount(12, OffsetDateTime.now(), ExternalAccountRole.ADMIN, 2000))
        val updatedSettings = service.updateNotificationsSettingsForAccountAndProject(12, 1, listOf(ProjectNotificationSettingDto(
            projectId = 1,
            externalAccountLinkId = 2000,
        )))

        StepVerifier.create(updatedSettings)
            .verifyComplete()
    }

    @Test
    fun `cannot set notifications for a linked external account that does not belong to the setting account`() {
        val account = makeAccount(OffsetDateTime.now(), 12, ExternalAccountRole.ADMIN)
        val project = makeProject(1)
        val existingNotificationSetting1 = ProjectNotificationSetting(
            id = 601,
            projectId = 1,
            makeLinkedExternalAccount(12, OffsetDateTime.now(), ExternalAccountRole.OWNER, 91),
            createTime = OffsetDateTime.now(),
        )
        every { accountRepository.findById(12) } returns Optional.of(account)
        every { projectRepository.findById(1) } returns Optional.of(project)
        every { projectNotificationSettingRepository.findByAccountIdAndProjectId(12, 1) } returns listOf(existingNotificationSetting1)
        every { projectNotificationSettingRepository.save(any()) } answers { firstArg<ProjectNotificationSetting>() }
        every { projectNotificationSettingRepository.delete(any()) } just Runs
        every { linkedExternalAccountRepository.findById(2000) } returns Optional.of(makeLinkedExternalAccount(13, OffsetDateTime.now(), ExternalAccountRole.ADMIN, 2000))
        val updatedSettings = service.updateNotificationsSettingsForAccountAndProject(12, 1, listOf(ProjectNotificationSettingDto(
            projectId = 1,
            externalAccountLinkId = 2000,
        )))

        StepVerifier.create(updatedSettings)
            .verifyComplete()
    }

    @Test
    fun `successful subscription update if currently unsubscribed`() {
        val account = makeAccount(OffsetDateTime.now(), 12, ExternalAccountRole.OWNER)
        account.linkedExternalAccounts.first { it.externalAccount.id == 1L }.apply {
            lastConfirmed = OffsetDateTime.now().minusMinutes(5)
            externalAccount.unsubscribeTime = OffsetDateTime.now()
        }

        every { accountRepository.findById(12) } returns Optional.of(account)
        every { linkedExternalAccountRepository.save(any()) } returnsArgument 0

        val result = service.updateLinkedExternalAccountSubscriptionStatus(12, 1, true)

        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete()

        verify(exactly = 1) { linkedExternalAccountRepository.save(match { it.externalAccount.unsubscribeTime == null }) }
    }

    @Test
    fun `successful subscription update if currently subscribed`() {
        val account = makeAccount(OffsetDateTime.now(), 12, ExternalAccountRole.OWNER)
        account.linkedExternalAccounts.first { it.externalAccount.id == 1L }.apply {
            lastConfirmed = OffsetDateTime.now().minusMinutes(5)
        }

        every { accountRepository.findById(12) } returns Optional.of(account)
        every { linkedExternalAccountRepository.save(any()) } returnsArgument 0

        val result = service.updateLinkedExternalAccountSubscriptionStatus(12, 1, false)

        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete()

        verify(exactly = 1) { linkedExternalAccountRepository.save(match { it.externalAccount.unsubscribeTime != null }) }
    }

    @Test
    fun `unsuccessful subscription update due to last confirmation too old`() {
        val account = makeAccount(OffsetDateTime.now(), 12, ExternalAccountRole.OWNER)
        account.linkedExternalAccounts.first { it.externalAccount.id == 1L }.lastConfirmed = OffsetDateTime.now().minusDays(5)

        every { accountRepository.findById(12) } returns Optional.of(account)

        val result = service.updateLinkedExternalAccountSubscriptionStatus(12, 1, false)

        StepVerifier.create(result)
            .expectError(LastConfirmationTooOldException::class.java)
            .verify()

        verify(exactly = 0) { linkedExternalAccountRepository.save(any()) }
    }

    @Test
    fun `unsuccessful subscription update due to not being an owner`() {
        val account = makeAccount(OffsetDateTime.now(), 12, ExternalAccountRole.PUBLISHER)

        every { accountRepository.findById(12) } returns Optional.of(account)

        val result = service.updateLinkedExternalAccountSubscriptionStatus(12, 1, true)

        StepVerifier.create(result)
            .expectError(PermissionDeniedException::class.java)
            .verify()

        verify(exactly = 0) { linkedExternalAccountRepository.save(any()) }
    }

    @Test
    fun `unsuccessful subscription update due to linked external account not found`() {
        val account = makeAccount(OffsetDateTime.now(), 12, ExternalAccountRole.OWNER)

        every { accountRepository.findById(12) } returns Optional.of(account)

        val result = service.updateLinkedExternalAccountSubscriptionStatus(12, 2, true)

        StepVerifier.create(result)
            .expectError(NoSuchElementException::class.java)
            .verify()

        verify(exactly = 0) { linkedExternalAccountRepository.save(any()) }
    }

    @Test
    fun `successful update with no subscription change`() {
        val account = makeAccount(OffsetDateTime.now(), 12, ExternalAccountRole.OWNER)
        account.linkedExternalAccounts.first { it.externalAccount.id == 1L }.lastConfirmed = OffsetDateTime.now().minusMinutes(5)

        every { accountRepository.findById(12) } returns Optional.of(account)

        val result = service.updateLinkedExternalAccountSubscriptionStatus(12, 1, true)

        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete()

        verify(exactly = 0) { linkedExternalAccountRepository.save(any()) }
    }

    private fun makeAccount(now: OffsetDateTime, id: Long = 12, role: ExternalAccountRole = ExternalAccountRole.OWNER) = Account(
        id = id,
        displayName = "test",
        createTime = now,
        linkedExternalAccounts = mutableSetOf(
            makeLinkedExternalAccount(id, now, role)
        ),
        cardanoSettings = "1111111111111111",
    )

    private fun makeLinkedExternalAccount(
        accountId: Long,
        now: OffsetDateTime,
        role: ExternalAccountRole,
        linkId: Long = 877L,
    ) = LinkedExternalAccount(
        id = linkId,
        accountId = accountId,
        externalAccount = makeExternalAccount(1, now),
        role = role,
        linkTime = now,
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

fun makeAccountDto(accountId: Long) = AccountDto(
    id = accountId,
    displayName = "test",
    createTime = OffsetDateTime.now(),
)
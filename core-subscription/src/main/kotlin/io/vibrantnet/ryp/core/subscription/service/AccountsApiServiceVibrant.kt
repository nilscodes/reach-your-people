package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.aspect.PointsClaim
import io.ryp.shared.model.*
import io.vibrantnet.ryp.core.subscription.model.*
import io.vibrantnet.ryp.core.subscription.persistence.*
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.OffsetDateTime
import java.util.*

@Service
class  AccountsApiServiceVibrant(
    val accountRepository: AccountRepository,
    val externalAccountRepository: ExternalAccountRepository,
    val linkedExternalAccountRepository: LinkedExternalAccountRepository,
    val projectRepository: ProjectRepository,
    val verifyService: VerifyService,
    val projectNotificationSettingRepository: ProjectNotificationSettingRepository,
) : AccountsApiService {

    @PointsClaim(
        points = "#points['referral']",
        category = "'referral'",
        claimId = "'referral-' + #result.id",
        accountId = "#referredBy",
    )
    @PointsClaim(
        points = "#points['signup']",
        category = "'signup'",
        claimId = "'signup-' + #result.id",
        accountId = "#result.id",
    )
    override fun createAccount(accountDto: AccountDto, referredBy: Long?): Mono<AccountDto> {
        val newAccount = Account(
            displayName = accountDto.displayName,
        )
        return Mono.just(accountRepository.save(newAccount).toDto())
    }

    override fun getAccountById(accountId: Long): Mono<AccountDto> {
        val account = accountRepository.findById(accountId)
        return if (account.isPresent) {
            Mono.just(account.get().toDto())
        } else {
            Mono.error(NoSuchElementException("No account with ID $accountId found"))
        }
    }

    override fun findAccountByProviderAndReferenceId(providerType: String, referenceId: String): Mono<AccountDto> {
        val externalAccount =
            externalAccountRepository.findByTypeAndReferenceId(providerType, referenceId).orElseThrow()
        return Mono.just(
            accountRepository.findByLinkedExternalAccountsExternalAccountId(externalAccount.id!!).first().toDto()
        )
    }

    @Transactional
    override fun getLinkedExternalAccounts(accountId: Long): Flux<LinkedExternalAccountDto> {
        val account = accountRepository.findById(accountId).orElseThrow()
        return Flux.fromIterable(account.linkedExternalAccounts.map { it.toDto() })
    }

    @Transactional
    override fun linkExternalAccount(externalAccountId: Long, accountId: Long): Mono<LinkedExternalAccountDto> {
        val account = accountRepository.findById(accountId).orElseThrow()
        val externalAccount = externalAccountRepository.findById(externalAccountId).orElseThrow()
        try {
            val newLinkedExternalAccount = linkedExternalAccountRepository.save(LinkedExternalAccount(
                accountId = accountId,
                externalAccount = externalAccount,
                role = ExternalAccountRole.OWNER,
                lastConfirmed = OffsetDateTime.now(),
            ))
            val defaultSettings = getDefaultSettingsForExternalAccount(externalAccount)
            newLinkedExternalAccount.settingsFromSet(defaultSettings)
            linkedExternalAccountRepository.updateSettings(newLinkedExternalAccount.id!!, newLinkedExternalAccount.settings)
            return Mono.just(newLinkedExternalAccount.toDto())
        } catch (e: DataIntegrityViolationException) {
            throw ExternalAccountAlreadyLinkedException("Account ${account.id} already linked to external account $externalAccountId", e)
        }
    }

    fun getDefaultSettingsForExternalAccount(externalAccount: ExternalAccount): Set<ExternalAccountSetting> {
        return when (externalAccount.type.lowercase()) {
            "cardano" -> EnumSet.of(
                ExternalAccountSetting.NON_FUNGIBLE_TOKEN_ANNOUNCEMENTS,
                ExternalAccountSetting.FUNGIBLE_TOKEN_ANNOUNCEMENTS,
                ExternalAccountSetting.RICH_FUNGIBLE_TOKEN_ANNOUNCEMENTS,
                ExternalAccountSetting.STAKEPOOL_ANNOUNCEMENTS,
                ExternalAccountSetting.DREP_ANNOUNCEMENTS,
            )
            else -> EnumSet.noneOf(ExternalAccountSetting::class.java)
        }
    }

    @Transactional
    override fun updateLinkedExternalAccount(
        accountId: Long,
        externalAccountId: Long,
        linkedExternalAccountPartial: LinkedExternalAccountPartialDto
    ): Mono<LinkedExternalAccountDto> {
        val account = accountRepository.findById(accountId).orElseThrow()
        val linkedExternalAccount = account.linkedExternalAccounts.find { it.externalAccount.id == externalAccountId }
        if (linkedExternalAccount != null) {
            if (linkedExternalAccount.role == ExternalAccountRole.OWNER) {
                if (linkedExternalAccountPartial.settings != null) {
                    linkedExternalAccount.settingsFromSet(linkedExternalAccountPartial.settings!!)
                    linkedExternalAccountRepository.updateSettings(linkedExternalAccount.id!!, linkedExternalAccount.settings) // Needs to be called separately
                }
                if (linkedExternalAccountPartial.lastConfirmed != null) {
                    linkedExternalAccount.lastConfirmed = linkedExternalAccountPartial.lastConfirmed
                }
                if (linkedExternalAccountPartial.lastTested != null) {
                    linkedExternalAccount.lastTested = linkedExternalAccountPartial.lastTested
                }
                // Technically don't need the save call here if only the settings change, but it's a good way to ensure to not introduce bugs when the method is changed later
                return Mono.just(linkedExternalAccountRepository.save(linkedExternalAccount).toDto())
            }
            return Mono.error(PermissionDeniedException("Cannot update linked external account $externalAccountId for account $accountId: User is not an owner of the external account for link ${linkedExternalAccount.id}"))
        }
        return Mono.error(NoSuchElementException("Failed to update linked external account $externalAccountId for account $accountId: Not found"))
    }

    @Transactional
    override fun unlinkExternalAccount(accountId: Long, externalAccountId: Long) {
        val account = accountRepository.findById(accountId).orElseThrow()
        val removed = account.linkedExternalAccounts.find { it.externalAccount.id == externalAccountId }
        if (removed != null) {
            linkedExternalAccountRepository.deleteDirectly(removed.id!!)
            // Delete external account if its the last link
            if (accountRepository.findByLinkedExternalAccountsExternalAccountId(externalAccountId).isEmpty()) {
                externalAccountRepository.deleteById(externalAccountId)
            }
        } else {
            throw NoSuchElementException("Failed to unlink external account $externalAccountId from account $accountId: Not found")
        }
    }

    override fun updateAccountById(accountId: Long, accountPartialDto: AccountPartialDto): Mono<AccountDto> {
        return updateAccountById(accountId, accountPartialDto, null)
    }

    @PointsClaim(
        points = "0",
        category = "'premium'",
        claimId = "'premium-' + #accountId",
        accountId = "#accountId",
    )
    override fun extendPremium(accountId: Long, premiumDuration: Duration): Mono<AccountDto> {
        return updateAccountById(accountId, AccountPartialDto(), premiumDuration)
    }

    private fun updateAccountById(accountId: Long, accountPartialDto: AccountPartialDto, premiumDuration: Duration?): Mono<AccountDto> {
        val accountOptional = accountRepository.findById(accountId)
        return if (accountOptional.isEmpty) {
            Mono.error(NoSuchElementException("No account with ID $accountId found"))
        } else {
            val account = accountOptional.get()
            var save = false
            if (accountPartialDto.displayName != null && accountPartialDto.displayName != account.displayName) {
                account.displayName = accountPartialDto.displayName
                save = true
            }
            if (premiumDuration != null) {
                val now = OffsetDateTime.now()
                val currentPremiumUntil = account.premiumUntil
                account.premiumUntil = if (currentPremiumUntil != null && currentPremiumUntil.isAfter(now)) {
                    currentPremiumUntil.plus(premiumDuration)
                } else {
                    now.plus(premiumDuration)
                }
                save = true
            }
            return if (save) {
                Mono.just(accountRepository.save(account).toDto())
            } else {
                Mono.just(account.toDto())
            }
        }
    }

    @Transactional
    override fun subscribeAccountToProject(
        accountId: Long,
        projectId: Long,
        newSubscription: NewSubscriptionDto
    ): Mono<NewSubscriptionDto> {
        val account = accountRepository.findById(accountId)
        val project = projectRepository.findById(projectId)
        return if (account.isPresent && project.isPresent) {
            account.get().subscriptions.removeIf { it.projectId == projectId }
            account.get().subscriptions.add(
                Subscription(
                    projectId = projectId,
                    status = newSubscription.status,
                )
            )
            accountRepository.save(account.get())
            Mono.just(newSubscription)
        } else if (account.isEmpty) {
            Mono.error(NoSuchElementException("No account with ID $accountId found"))
        } else {
            Mono.error(NoSuchElementException("No project with ID $projectId found"))
        }
    }

    @Transactional
    override fun unsubscribeAccountFromProject(accountId: Long, projectId: Long): Mono<Unit> {
        val account = accountRepository.findById(accountId)
        return if (account.isPresent) {
            account.get().subscriptions.removeIf { it.projectId == projectId }
            accountRepository.save(account.get())
            Mono.empty()
        } else {
            Mono.error(NoSuchElementException("No account with ID $accountId found"))
        }
    }

    @Transactional
    override fun getAllSubscriptionsForAccount(accountId: Long): Flux<ProjectSubscriptionDto> {
        val account = accountRepository.findById(accountId).orElseThrow()
        val walletBasedSubscriptions = getCardanoWalletBasedSubscriptions(account)

        val explicitSubscriptions = Flux.fromIterable(account.subscriptions.map { it.toDto() })

        return mergeExplicitAndWalletBasedSubscriptions(explicitSubscriptions, walletBasedSubscriptions)
    }

    @Transactional
    override fun getSettingsForAccount(accountId: Long): Mono<SettingsDto> {
        val account = accountRepository.findById(accountId).orElseThrow()
        return Mono.just(SettingsDto(account.settings.map { it.toDto() }.toSet()))
    }

    @Transactional
    override fun updateAccountSetting(accountId: Long, settingName: String, setting: SettingDto): Mono<SettingDto> {
        val account = accountRepository.findById(accountId).orElseThrow()
        val newSetting = EmbeddableSetting(
            name = settingName,
            value = setting.value,
        )
        account.settings.removeIf { it.name == settingName }
        account.settings.add(newSetting)
        accountRepository.save(account)
        return Mono.just(newSetting.toDto())
    }

    @Transactional
    override fun deleteAccountSetting(accountId: Long, settingName: String): Mono<Unit> {
        val account = accountRepository.findById(accountId).orElseThrow()
        account.settings.removeIf { it.name == settingName }
        accountRepository.save(account)
        return Mono.empty()
    }

    override fun getNotificationsSettingsForAccountAndProject(
        accountId: Long,
        projectId: Long,
    ) = Flux.fromIterable(projectNotificationSettingRepository.findByAccountIdAndProjectId(accountId, projectId).map { it.toDto() })

    @Transactional
    override fun updateNotificationsSettingsForAccountAndProject(
        accountId: Long,
        projectId: Long,
        projectNotificationSettings: List<ProjectNotificationSettingDto>,
    ): Flux<ProjectNotificationSettingDto> {
        accountRepository.findById(accountId).orElseThrow()
        projectRepository.findById(projectId).orElseThrow()
        val newNotifications = removeUnusedNotifications(accountId, projectId, projectNotificationSettings)
        newNotifications.addAll(addNewNotifications(projectNotificationSettings, accountId, projectId, newNotifications))
        return Flux.fromIterable(newNotifications)
    }

    private fun addNewNotifications(
        projectNotificationSetting: List<ProjectNotificationSettingDto>,
        accountId: Long,
        projectId: Long,
        existingNotifications: List<ProjectNotificationSettingDto>
    ): List<ProjectNotificationSettingDto> {
        val newNotifications = mutableListOf<ProjectNotificationSettingDto>()
        for (projectNotification in projectNotificationSetting) {
            if (existingNotifications.any { it.externalAccountLinkId == projectNotification.externalAccountLinkId }) {
                continue
            }
            val linkedExternalAccount =
                linkedExternalAccountRepository.findById(projectNotification.externalAccountLinkId).orElseThrow()
            if (linkedExternalAccount.accountId == accountId && linkedExternalAccount.role == ExternalAccountRole.OWNER) {
                val newNotification = ProjectNotificationSetting(
                    linkedExternalAccount = linkedExternalAccount,
                    projectId = projectId,
                    createTime = OffsetDateTime.now(),
                )
                newNotifications.add(projectNotificationSettingRepository.save(newNotification).toDto())
            }
        }
        return newNotifications
    }

    private fun removeUnusedNotifications(
        accountId: Long,
        projectId: Long,
        projectNotificationSetting: List<ProjectNotificationSettingDto>
    ): MutableList<ProjectNotificationSettingDto> {
        val newNotifications = mutableListOf<ProjectNotificationSettingDto>()
        val currentNotifications =
            projectNotificationSettingRepository.findByAccountIdAndProjectId(accountId, projectId)
        for (currentNotification in currentNotifications) {
            if (projectNotificationSetting.none { it.externalAccountLinkId == currentNotification.linkedExternalAccount.id }) {
                projectNotificationSettingRepository.delete(currentNotification)
            } else {
                newNotifications.add(currentNotification.toDto())
            }
        }
        return newNotifications
    }

    /**
     * Merge so that the default status is SUBSCRIBED if any of the subscriptions are wallet-subscribed
     *  and the current status is set to whatever the explicit subscription setting is, if any is available
     */
    private fun mergeExplicitAndWalletBasedSubscriptions(
        explicitSubscriptions: Flux<ProjectSubscriptionDto>?,
        walletBasedSubscriptions: Flux<ProjectSubscriptionDto>
    ): Flux<ProjectSubscriptionDto> =
        Flux.merge(explicitSubscriptions, walletBasedSubscriptions)
            .groupBy { it.projectId }
            .flatMap { groupedFlux ->
                groupedFlux.reduce { acc, current ->
                    ProjectSubscriptionDto(
                        projectId = acc.projectId,
                        defaultStatus = if (current.defaultStatus == DefaultSubscriptionStatus.SUBSCRIBED || acc.defaultStatus == DefaultSubscriptionStatus.SUBSCRIBED) DefaultSubscriptionStatus.SUBSCRIBED else DefaultSubscriptionStatus.UNSUBSCRIBED,
                        currentStatus = if (acc.currentStatus != SubscriptionStatus.DEFAULT) acc.currentStatus else current.currentStatus
                    )
                }
            }

    /**
     * Get wallet-based subscriptions for Cardano by querying the verify service for policies in the account's wallets,
     * delegated stakepools, and then get associated projects. Every policy or stakepool that is present will result
     * in a default-subscription, unless turned off for that wallet.
     */
    private fun getCardanoWalletBasedSubscriptions(account: Account): Flux<ProjectSubscriptionDto> {
        val ownedCardanoWallets = account.linkedExternalAccounts.filter { it.externalAccount.type == "cardano" }
            .map { it.toDto() }

        val policyBasedSubscriptions = getCardanoWalletTokenBasedSubscriptions(ownedCardanoWallets)
        val stakepoolBasedSubscriptions = getCardanoWalletStakepoolBasedSubscriptions(ownedCardanoWallets)

        return Flux.merge(policyBasedSubscriptions, stakepoolBasedSubscriptions)
    }

    private fun getCardanoWalletTokenBasedSubscriptions(ownedCardanoWallets: List<LinkedExternalAccountDto>) =
        Flux.fromIterable(ownedCardanoWallets)
            .filter { it.settings.contains(ExternalAccountSetting.NON_FUNGIBLE_TOKEN_ANNOUNCEMENTS) }
            .flatMap { verifyService.getPoliciesInWallet(it.externalAccount.referenceId) }
            .map { it.policyIdWithOptionalAssetFingerprint }
            .collectList()
            .flatMapMany { policyIds ->
                if (policyIds.isEmpty()) {
                    Flux.empty()
                } else {
                    Flux.fromIterable(projectRepository.findByPoliciesPolicyIdIn(policyIds))
                        .map { project ->
                            ProjectSubscriptionDto(
                                projectId = project.id!!,
                                defaultStatus = DefaultSubscriptionStatus.SUBSCRIBED,
                                currentStatus = SubscriptionStatus.DEFAULT
                            )
                        }
                }
            }


    private fun getCardanoWalletStakepoolBasedSubscriptions(ownedCardanoWallets: List<LinkedExternalAccountDto>) =
        Flux.fromIterable(ownedCardanoWallets)
            .filter { it.settings.contains(ExternalAccountSetting.STAKEPOOL_ANNOUNCEMENTS) }
            .flatMap { verifyService.getStakepoolDetailsForStakeAddress(it.externalAccount.referenceId) }
            .map { it.poolHash }
            .collectList()
            .flatMapMany { poolIds ->
                if (poolIds.isEmpty()) {
                    Flux.empty()
                } else {
                    Flux.fromIterable(projectRepository.findByStakepoolsPoolHashIn(poolIds))
                        .map { project ->
                            ProjectSubscriptionDto(
                                projectId = project.id!!,
                                defaultStatus = DefaultSubscriptionStatus.SUBSCRIBED,
                                currentStatus = SubscriptionStatus.DEFAULT
                            )
                        }
                }
            }

}
package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.aspect.PointsClaim
import io.ryp.shared.model.ExternalAccountRole
import io.ryp.shared.model.LinkedExternalAccountDto
import io.ryp.shared.model.LinkedExternalAccountPartialDto
import io.vibrantnet.ryp.core.subscription.model.*
import io.vibrantnet.ryp.core.subscription.persistence.*
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@Service
class AccountsApiServiceVibrant(
    val accountRepository: AccountRepository,
    val externalAccountRepository: ExternalAccountRepository,
    val linkedExternalAccountRepository: LinkedExternalAccountRepository,
    val projectRepository: ProjectRepository,
    val verifyService: VerifyService,
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
            return Mono.just(newLinkedExternalAccount.toDto())
        } catch (e: DataIntegrityViolationException) {
            throw ExternalAccountAlreadyLinkedException("Account ${account.id} already linked to external account $externalAccountId", e)
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
            if (linkedExternalAccount.externalAccount.type == "cardano") {
                if (linkedExternalAccount.role == ExternalAccountRole.OWNER) {
                    if (linkedExternalAccountPartial.settings != null) {
                        linkedExternalAccount.settingsFromSet(linkedExternalAccountPartial.settings!!)
                        linkedExternalAccountRepository.updateSettings(linkedExternalAccount.id!!, linkedExternalAccount.settings) // Needs to be called separately
                    }
                    if (linkedExternalAccountPartial.lastConfirmed != null) {
                        linkedExternalAccount.lastConfirmed = linkedExternalAccountPartial.lastConfirmed
                    }
                    // Technically don't need the save call here if only the settings change, but it's a good way to ensure to not introduce bugs when the method is changed later
                    return Mono.just(linkedExternalAccountRepository.save(linkedExternalAccount).toDto())
                }
                throw PermissionDeniedException("Cannot update linked external account $externalAccountId for account $accountId: User is not an owner of the external account for link ${linkedExternalAccount.id}")
            }
            throw IncompatibleExternalAccountChangeException("Cannot update linked external account $externalAccountId for account $accountId: Not a Cardano wallet for link ${linkedExternalAccount.id}")
        }
        throw NoSuchElementException("Failed to update linked external account $externalAccountId for account $accountId: Not found")
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
        val account = accountRepository.findById(accountId).orElseThrow()
        if (accountPartialDto.displayName != null && accountPartialDto.displayName != account.displayName) {
            account.displayName = accountPartialDto.displayName
            return Mono.just(accountRepository.save(account).toDto())
        }
        return Mono.just(account.toDto())
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
        if (account.isPresent) {
            account.get().subscriptions.removeIf { it.projectId == projectId }
            accountRepository.save(account.get())
            return Mono.empty()
        } else {
            return Mono.error(NoSuchElementException("No account with ID $accountId found"))
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
     * then get associated projects. Every policy that is present will result in a default-subscription.
     */
    private fun getCardanoWalletBasedSubscriptions(account: Account): Flux<ProjectSubscriptionDto> {
        val ownedCardanoWallets = account.linkedExternalAccounts.filter { it.externalAccount.type == "cardano" }
            .map { it.externalAccount.referenceId }

        return Flux.fromIterable(ownedCardanoWallets)
            .flatMap { verifyService.getPoliciesInWallet(it) }
            .map { it.policyIdWithOptionalAssetFingerprint }
            .collectList()
            .flatMapMany { policyIds ->
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
}
package io.vibrantnet.ryp.core.subscription.service

import io.vibrantnet.ryp.core.subscription.model.*
import io.vibrantnet.ryp.core.subscription.persistence.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AccountsApiServiceVibrant(
    val accountRepository: AccountRepository,
    val externalAccountRepository: ExternalAccountRepository,
    val projectRepository: ProjectRepository,
): AccountsApiService {

    override fun createAccount(accountDto: AccountDto): Mono<AccountDto> {
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
        val externalAccount = externalAccountRepository.findByTypeAndReferenceId(providerType, referenceId).orElseThrow()
        return Mono.just(accountRepository.findByLinkedExternalAccountsExternalAccountId(externalAccount.id!!).first().toDto())
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
        val newLinkedExternalAccount = LinkedExternalAccount(
            externalAccount = externalAccount,
            role = LinkedExternalAccountDto.ExternalAccountRole.OWNER,
        )
        val added = account.linkedExternalAccounts.add(newLinkedExternalAccount)
        if (added) {
            accountRepository.save(account)
            return Mono.just(newLinkedExternalAccount.toDto())
        } else {
            throw ExternalAccountAlreadyLinkedException("Account ${account.id} already linked to external account $externalAccountId")
        }
    }

    @Transactional
    override fun unlinkExternalAccount(accountId: Long, externalAccountId: Long) {
        val account = accountRepository.findById(accountId).orElseThrow()
        val removed = account.linkedExternalAccounts.removeIf { it.externalAccount.id == externalAccountId }
        if (removed) {
            accountRepository.save(account)
            // Delete external account if its the last link
            if (accountRepository.findByLinkedExternalAccountsExternalAccountId(externalAccountId).isEmpty()) {
                externalAccountRepository.deleteById(externalAccountId)
            }
        } else {
            throw NoSuchElementException("Failed to unlink external account: Not found")
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
    override fun subscribeAccountToProject(accountId: Long, projectId: Long, newSubscription: NewSubscriptionDto): Mono<NewSubscriptionDto> {
        val account = accountRepository.findById(accountId)
        val project = projectRepository.findById(projectId)
        return if (account.isPresent && project.isPresent) {
            account.get().subscriptions.removeIf { it.projectId == projectId }
            account.get().subscriptions.add(Subscription(
                projectId = projectId,
                status = newSubscription.status,
            ))
            accountRepository.save(account.get())
            Mono.just(newSubscription)
        } else if (account.isEmpty) {
            Mono.error(NoSuchElementException("No account with ID $accountId found"))
        } else {
            Mono.error(NoSuchElementException("No project with ID $projectId found"))
        }
    }

    @Transactional
    override fun unsubscribeAccountFromProject(accountId: Long, projectId: Long): Mono<Void> {
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
        return Flux.fromIterable(account.subscriptions.map { it.toDto() })
    }
}
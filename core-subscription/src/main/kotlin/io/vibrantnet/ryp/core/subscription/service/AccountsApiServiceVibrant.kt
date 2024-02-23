package io.vibrantnet.ryp.core.subscription.service

import io.vibrantnet.ryp.core.subscription.model.AccountDto
import io.vibrantnet.ryp.core.subscription.model.AccountPartialDto
import io.vibrantnet.ryp.core.subscription.model.ExternalAccountAlreadyLinkedException
import io.vibrantnet.ryp.core.subscription.model.LinkedExternalAccountDto
import io.vibrantnet.ryp.core.subscription.persistence.Account
import io.vibrantnet.ryp.core.subscription.persistence.AccountRepository
import io.vibrantnet.ryp.core.subscription.persistence.LinkedExternalAccount
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AccountsApiServiceVibrant(
    val accountRepository: AccountRepository,
): AccountsApiService {

    override fun createAccount(accountDto: AccountDto): Mono<AccountDto> {
        val newAccount = Account(
            displayName = accountDto.displayName,
        )
        return Mono.just(accountRepository.save(newAccount).toDto())
    }

    override fun getAccountById(accountId: Long): Mono<AccountDto> {
        return Mono.just(accountRepository.findById(accountId).orElseThrow().toDto())
    }

    @Transactional
    override fun getLinkedExternalAccounts(accountId: Long): Flux<LinkedExternalAccountDto> {
        val account = accountRepository.findById(accountId).orElseThrow()
        return Flux.fromIterable(account.linkedExternalAccounts.map { it.toDto() })
    }

    @Transactional
    override fun linkExternalAccount(externalAccountId: Long, accountId: Long): Mono<LinkedExternalAccountDto> {
        val account = accountRepository.findById(accountId).orElseThrow()
        val newLinkedExternalAccount = LinkedExternalAccount(
            externalAccountId = externalAccountId,
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
        val removed = account.linkedExternalAccounts.removeIf { it.externalAccountId == externalAccountId }
        if (removed) {
            accountRepository.save(account)
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

}
package io.vibrantnet.ryp.core.subscription.service

import io.vibrantnet.ryp.core.subscription.model.ExternalAccountDto
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccount
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ExternalAccountsApiServiceVibrant(
    val externalAccountRepository: ExternalAccountRepository,
) : ExternalAccountsApiService {
    override fun createExternalAccount(externalAccountDto: ExternalAccountDto): Mono<ExternalAccountDto> {
        // TODO: Valid dynamically registered account types can be checked here before saving
        val newExternalAccount = ExternalAccount(
            referenceId = externalAccountDto.referenceId,
            referenceName = externalAccountDto.referenceName,
            type = externalAccountDto.type,
        )
        return Mono.just(externalAccountRepository.save(newExternalAccount).toDto())
    }
}
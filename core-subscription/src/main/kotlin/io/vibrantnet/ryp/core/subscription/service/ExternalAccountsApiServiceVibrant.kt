package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.model.ExternalAccountDto
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
            displayName = externalAccountDto.displayName,
            type = externalAccountDto.type,
            metadata = externalAccountDto.metadata.let {
                if (it != null) {
                    java.util.Base64.getDecoder().decode(it)
                } else {
                    null
                }
            }
        )
        return Mono.just(externalAccountRepository.save(newExternalAccount).toDto())
    }

    override fun findExternalAccountByProviderAndReferenceId(providerType: String, referenceId: String): Mono<ExternalAccountDto> {
        val externalAccount = externalAccountRepository.findByTypeAndReferenceId(providerType, referenceId)
        return if (externalAccount.isPresent) {
            Mono.just(externalAccount.get().toDto())
        } else {
            Mono.error(NoSuchElementException("No external account with provider type $providerType and reference ID $referenceId found"))
        }
    }
}
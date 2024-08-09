package io.vibrantnet.ryp.core.subscription.service

import io.ryp.shared.model.ExternalAccountDto
import io.vibrantnet.ryp.core.subscription.model.UnsubscribeFromEmailRequest
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccount
import io.vibrantnet.ryp.core.subscription.persistence.ExternalAccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.*

@Service
class ExternalAccountsApiServiceVibrant(
    val externalAccountRepository: ExternalAccountRepository,
) : ExternalAccountsApiService {
    override fun createExternalAccount(externalAccountDto: ExternalAccountDto): Mono<ExternalAccountDto> {
        val newExternalAccount = ExternalAccount(
            referenceId = externalAccountDto.referenceId,
            referenceName = externalAccountDto.referenceName,
            displayName = externalAccountDto.displayName,
            type = externalAccountDto.type,
            metadata = externalAccountDto.metadata.let {
                if (it != null) {
                    Base64.getDecoder().decode(it)
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

    @Transactional
    override fun unsubscribeFromEmail(unsubscribeFromEmailRequest: UnsubscribeFromEmailRequest): Mono<Unit> {
        val externalMailAccountWithEmailAddress = externalAccountRepository.findByTypeAndReferenceId("email", unsubscribeFromEmailRequest.email)
        unsubscribeExternalAccountIfPresent(externalMailAccountWithEmailAddress)
        val googleMailAccountWithEmailAddress = externalAccountRepository.findByTypeAndReferenceName("google", unsubscribeFromEmailRequest.email)
        unsubscribeExternalAccountIfPresent(googleMailAccountWithEmailAddress)
        return Mono.empty()
    }

    private fun unsubscribeExternalAccountIfPresent(externalMailAccountWithEmailAddress: Optional<ExternalAccount>) {
        externalMailAccountWithEmailAddress.ifPresent {
            it.unsubscribeTime = OffsetDateTime.now()
            externalAccountRepository.save(it)
        }
    }
}
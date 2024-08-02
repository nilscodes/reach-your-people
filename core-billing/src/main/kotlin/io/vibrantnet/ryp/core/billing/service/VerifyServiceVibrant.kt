package io.vibrantnet.ryp.core.billing.service

import io.ryp.cardano.model.TransactionSummaryDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

@Service
class VerifyServiceVibrant(
    @Qualifier("coreVerificationClient")
    private val coreVerificationClient: WebClient,
) : VerifyService {
    override fun getTransactionSummary(transactionHash: String) = coreVerificationClient.get()
        .uri("/transactions/$transactionHash/summary")
        .retrieve()
        .bodyToMono(TransactionSummaryDto::class.java)
        .onErrorResume(WebClientResponseException.NotFound::class.java) {
            Mono.error(NoSuchElementException("No transaction found with hash $transactionHash"))
        }
}
package io.vibrantnet.ryp.core.publishing.service

import io.ryp.cardano.model.StakepoolDetailsDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

@Service
class VerifyServiceVibrant(
    @Qualifier("coreVerificationClient")
    private val coreVerificationClient: WebClient,
): VerifyService {
    override fun verifyCip66(
        policyId: String,
        serviceName: String,
        referenceId: String,
    ): Mono<Boolean> {
        return coreVerificationClient.get()
            .uri("/cip66/$policyId/$serviceName/$referenceId")
            .retrieve()
            .bodyToMono(Boolean::class.java)
            .onErrorResume(WebClientResponseException::class.java) { ex ->
                if (ex.statusCode.value() == 404) Mono.just(false) else Mono.error(ex)
            }
    }

    override fun getStakepoolDetails(poolHash: String): Mono<StakepoolDetailsDto> {
        return coreVerificationClient.get()
            .uri("/pools/$poolHash")
            .retrieve()
            .bodyToMono(StakepoolDetailsDto::class.java)
    }
}
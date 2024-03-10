package io.vibrantnet.ryp.core.publishing.service

import io.ryp.shared.model.LinkedExternalAccountDto
import io.ryp.shared.model.ProjectDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SubscriptionServiceVibrant(
    @Qualifier("coreSubscriptionClient")
    private val coreSubscriptionClient: WebClient,
) : SubscriptionService {
    override fun getProject(projectId: Long): Mono<ProjectDto> {
        return coreSubscriptionClient.get()
            .uri("/projects/$projectId")
            .retrieve()
            .bodyToMono(ProjectDto::class.java)
    }

    override fun getLinkedExternalAccounts(accountId: Long): Flux<LinkedExternalAccountDto> {
        return coreSubscriptionClient.get()
            .uri("/accounts/$accountId/externalaccounts")
            .retrieve()
            .bodyToFlux(LinkedExternalAccountDto::class.java)
    }
}
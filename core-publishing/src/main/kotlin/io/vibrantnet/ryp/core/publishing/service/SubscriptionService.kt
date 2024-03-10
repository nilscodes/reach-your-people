package io.vibrantnet.ryp.core.publishing.service

import io.ryp.shared.model.LinkedExternalAccountDto
import io.ryp.shared.model.ProjectDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface SubscriptionService {
    fun getProject(projectId: Long): Mono<ProjectDto>
    fun getLinkedExternalAccounts(accountId: Long): Flux<LinkedExternalAccountDto>
}
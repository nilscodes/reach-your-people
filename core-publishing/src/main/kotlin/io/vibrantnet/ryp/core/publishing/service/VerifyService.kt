package io.vibrantnet.ryp.core.publishing.service

import reactor.core.publisher.Mono

fun interface VerifyService {
    fun verifyCip66(
        policyId: String,
        serviceName: String,
        referenceId: String,
    ): Mono<Boolean>
}
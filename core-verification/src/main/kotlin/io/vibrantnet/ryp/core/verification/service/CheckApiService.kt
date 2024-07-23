package io.vibrantnet.ryp.core.verification.service

import io.vibrantnet.ryp.core.verification.model.Cip66PayloadDto
import reactor.core.publisher.Mono

interface CheckApiService {
    fun getCip66InfoByPolicyId(policyId: String): Mono<Cip66PayloadDto>
    fun verify(policyId: String, providerType: String, referenceId: String): Mono<Boolean>
}
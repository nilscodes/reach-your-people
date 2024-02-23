package io.vibrantnet.ryp.core.verification.service

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.vibrantnet.ryp.core.verification.model.Cip66PayloadDto
import io.vibrantnet.ryp.core.verification.model.IamXDid
import io.vibrantnet.ryp.core.verification.persistence.Cip66Dao
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class CheckApiServiceVibrant(
    private val cip66Dao: Cip66Dao,
    @Qualifier("ipfsClient") private val ipfsClient: WebClient
) : CheckApiService {
    override fun getCip66InfoByPolicyId(policyId: String): Mono<Cip66PayloadDto> = cip66Dao.getCip66Payload(policyId)

    override fun verify(policyId: String, serviceName: String, referenceId: String): Mono<Boolean> {
        return cip66Dao.getCip66Payload(policyId)
            .flatMap { policyInfo ->
                val ipfsUrl = policyInfo.policies[PolicyId(policyId)]?.files?.firstOrNull()?.src

                if (ipfsUrl != null) {
                    val ipfsHash = ipfsUrl.split("/").last()
                    ipfsClient.get()
                        .uri("/$ipfsHash")
                        .retrieve()
                        .bodyToMono(IamXDid::class.java)
                        .map {
                            it.isValidMatch(serviceName, referenceId)
                        }
                } else {
                    Mono.just(false)
                }
            }
    }

}

package io.vibrantnet.ryp.core.verification.persistence

import io.vibrantnet.ryp.core.verification.model.PartialPoolInfo
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "blockfrost")
class Cip22DaoBlockfrost(
    @Qualifier("blockfrostClient") private val blockfrostClient: WebClient,
) : Cip22Dao {

    @OptIn(ExperimentalStdlibApi::class)
    override fun getVrfVerificationKeyHashForPool(poolHash: String): Mono<ByteArray> {
        return blockfrostClient.get()
            .uri("/pools/$poolHash")
            .retrieve()
            .onStatus({ status -> status == HttpStatus.NOT_FOUND }) { _ ->
                Mono.error(NoSuchElementException("Pool info not found in Blockfrost for pool hash $poolHash."))
            }
            .bodyToMono(PartialPoolInfo::class.java)
            .flatMap {
                Mono.just(it.vrfKeyHash.hexToByteArray())
            }
    }
}
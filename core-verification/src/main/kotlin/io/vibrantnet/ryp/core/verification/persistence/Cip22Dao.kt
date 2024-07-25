package io.vibrantnet.ryp.core.verification.persistence

import reactor.core.publisher.Mono

fun interface Cip22Dao {
    fun getVrfVerificationKeyHashForPool(poolHash: String): Mono<ByteArray>
}
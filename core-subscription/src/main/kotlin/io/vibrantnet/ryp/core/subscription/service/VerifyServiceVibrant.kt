package io.vibrantnet.ryp.core.subscription.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.ryp.shared.model.TokenOwnershipInfoWithAssetCount
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.temporal.ChronoUnit

@Service
class VerifyServiceVibrant(
    @Qualifier("coreVerificationClient")
    val coreVerificationClient: WebClient,
    val redisTemplate: RedisTemplate<String, Any>,
    val objectMapper: ObjectMapper,
): VerifyService {
    override fun getPoliciesInWallet(stakeAddress: String): Flux<TokenOwnershipInfoWithAssetCount> {
        val cachedRaw = redisTemplate.opsForList().range(
            "stakeAddress:$stakeAddress",
            0,
            -1,
        )
        if (!cachedRaw.isNullOrEmpty()) {
            return Flux.fromIterable(cachedRaw.map {
                objectMapper.convertValue(it, TokenOwnershipInfoWithAssetCount::class.java)
            })
        }
        return coreVerificationClient.get()
            .uri("/stake/$stakeAddress/assetcounts")
            .retrieve()
            .bodyToFlux(TokenOwnershipInfoWithAssetCount::class.java)
            .doOnNext {
                redisTemplate.opsForList().rightPushAll(
                    "stakeAddress:$stakeAddress",
                    it,
                )
            }
            .doOnComplete {
                redisTemplate.expire(
                    "stakeAddress:$stakeAddress",
                    Duration.of(10, ChronoUnit.MINUTES),
                )
            }
    }
}
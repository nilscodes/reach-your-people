package io.vibrantnet.ryp.core.subscription.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.ryp.cardano.model.DRepDetailsDto
import io.ryp.cardano.model.StakepoolDetailsDto
import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

@Service
class VerifyServiceVibrant(
    @Qualifier("coreVerificationClient")
    private val coreVerificationClient: WebClient,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper,
): VerifyService {
    override fun getPoliciesInWallet(stakeAddress: String): Flux<TokenOwnershipInfoWithAssetCount> {
        val cachedRaw = redisTemplate.opsForList().range(
            "stakeAddress:assetcounts:$stakeAddress",
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
                    "stakeAddress:assetcounts:$stakeAddress",
                    it,
                )
            }
            .doOnComplete {
                redisTemplate.expire(
                    "stakeAddress:assetcounts:$stakeAddress",
                    10,
                    TimeUnit.MINUTES,
                )
            }
    }

    override fun getStakepoolDetailsForStakeAddress(stakeAddress: String): Mono<StakepoolDetailsDto> {
        val cachedRaw = redisTemplate.opsForValue().get("stakeAddress:pool:$stakeAddress")
        return if (cachedRaw != null) {
            Mono.just(objectMapper.convertValue(cachedRaw, StakepoolDetailsDto::class.java))
        } else {
            coreVerificationClient.get()
                .uri("/stake/$stakeAddress/pool")
                .retrieve()
                .bodyToMono(StakepoolDetailsDto::class.java)
                .onErrorResume(WebClientResponseException::class.java) { ex ->
                    if (ex.statusCode.value() == 404) Mono.empty() else Mono.error(ex)
                }
                .doOnNext {
                    redisTemplate.opsForValue().set("stakeAddress:pool:$stakeAddress", it, 10, TimeUnit.MINUTES)
                }
        }
    }

    override fun getDRepDetailsForStakeAddress(stakeAddress: String): Mono<DRepDetailsDto> {
        val cachedRaw = redisTemplate.opsForValue().get("stakeAddress:drep:$stakeAddress")
        return if (cachedRaw != null) {
            Mono.just(objectMapper.convertValue(cachedRaw, DRepDetailsDto::class.java))
        } else {
            coreVerificationClient.get()
                .uri("/stake/$stakeAddress/drep")
                .retrieve()
                .bodyToMono(DRepDetailsDto::class.java)
                .onErrorResume(WebClientResponseException::class.java) { ex ->
                    if (ex.statusCode.value() == 404) Mono.empty() else Mono.error(ex)
                }
                .doOnNext {
                    redisTemplate.opsForValue().set("stakeAddress:drep:$stakeAddress", it, 10, TimeUnit.MINUTES)
                }
        }
    }

}
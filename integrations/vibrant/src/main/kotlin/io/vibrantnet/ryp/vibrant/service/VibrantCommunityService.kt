package io.vibrantnet.ryp.vibrant.service

import io.vibrantnet.ryp.vibrant.model.ExternalAccountDto
import io.vibrantnet.ryp.vibrant.model.VerificationDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class VibrantCommunityService(
    @Qualifier("communityClient")
    private val communityClient: WebClient,
) {
    fun getVerificationsForDiscordUserId(discordUserId: Long): Flux<VerificationDto> {
        return getExternalAccountByDiscordId(discordUserId).
            flatMapMany {
                communityClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/externalaccounts/${it.id}/verifications")
                        .build()
                }.retrieve()
                .bodyToFlux(VerificationDto::class.java)
            }
    }

    private fun getExternalAccountByDiscordId(discordUserId: Long): Mono<ExternalAccountDto> {
        return communityClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/externalaccounts/discord/$discordUserId")
                    .build()
            }.retrieve()
            .bodyToMono(ExternalAccountDto::class.java)
            .doOnError(WebClientResponseException.NotFound::class.java) {
                throw NoSuchElementException("No external account found for discord user $discordUserId")
            }
    }

}
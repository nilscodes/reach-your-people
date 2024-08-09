package io.vibrantnet.ryp.core.verification.controller

import io.mockk.every
import io.mockk.mockk
import io.ryp.cardano.model.StakepoolDetailsDto
import io.ryp.cardano.model.TokenOwnershipInfoWithAssetCount
import io.vibrantnet.ryp.core.loadJsonFromResource
import io.vibrantnet.ryp.core.verification.service.StakeApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@WebFluxTest(
    controllers = [StakeApiController::class, ApiExceptionHandler::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
class StakeApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun stakeApiService() = mockk<StakeApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var stakeApiService: StakeApiService

    @Test
    fun `getting asset counts for a stake address works`() {
        val stakeAddress = "stake1baked"
        every { stakeApiService.getMultiAssetCountForStakeAddress(stakeAddress) } answers {
            Flux.fromIterable(listOf(
                TokenOwnershipInfoWithAssetCount("stake1baked", "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f", 12),
                TokenOwnershipInfoWithAssetCount("stake1baked", "df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058", 2)
            ))
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-multi-asset-count-for-stake-address-response.json")

        webClient.get()
            .uri("/stake/${stakeAddress}/assetcounts")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }

    @Test
    fun `getting stake pool details for a stake address works`() {
        val stakeAddress = "stake1baked"
        every { stakeApiService.getStakepoolDetailsForStakeAddress(stakeAddress) } answers {
            Mono.just(StakepoolDetailsDto(
                poolHash = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
                ticker = "VIBRN",
                name = "Vibrant",
                homepage = "https://vibrantnet.io",
                description = "Vibrant Stake Pool"
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/test-get-stakepool-details-for-stake-address-response.json")

        webClient.get()
            .uri("/stake/${stakeAddress}/pool")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }
}
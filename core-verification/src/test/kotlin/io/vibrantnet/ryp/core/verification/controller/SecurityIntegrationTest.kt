package io.vibrantnet.ryp.core.verification.controller

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.verification.service.StakeApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("secured", "test")
class SecurityIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun stakeApiService() = mockk<StakeApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var stakeApiService: StakeApiService

    @Test
    fun `getting a specific project is secured properly and returns the right object`() {
        val stakeAddress = "stakeAddress"
        every { stakeApiService.getMultiAssetCountForStakeAddress(stakeAddress) } answers {
            Flux.fromIterable(listOf())
        }

        webClient.get()
            .uri("/stake/$stakeAddress/assetcounts")
            .headers { it.set(HttpHeaders.AUTHORIZATION, "deadend") }
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `not providing an API key results in a 401`() {
        webClient.get()
            .uri("/stake/stakeAddress/assetcounts")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `health endpoint is not secured`() {
        webClient.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk
    }

}
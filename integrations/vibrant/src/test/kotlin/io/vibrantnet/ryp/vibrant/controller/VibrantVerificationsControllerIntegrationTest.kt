package io.vibrantnet.ryp.vibrant.controller

import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.vibrant.TestSecurityConfiguration
import io.vibrantnet.ryp.vibrant.loadJsonFromResource
import io.vibrantnet.ryp.vibrant.model.BlockchainType
import io.vibrantnet.ryp.vibrant.model.VerificationDto
import io.vibrantnet.ryp.vibrant.service.VibrantCommunityService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import java.time.OffsetDateTime
import java.util.*

@WebFluxTest(controllers = [VibrantVerificationsController::class])
@Import(TestSecurityConfiguration::class)
internal class VibrantVerificationsControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun vibrantCommunityService() = mockk<VibrantCommunityService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var vibrantCommunityService: VibrantCommunityService

    @Test
    fun `getting verification for discord user id works`() {
        val discordUserId = 376L
        val fixedDate = Date.from(OffsetDateTime.parse("2024-07-06T12:00:00Z").toInstant())
        every { vibrantCommunityService.getVerificationsForDiscordUserId(discordUserId) } answers {
            Flux.fromIterable(
                listOf(
                    VerificationDto(1, 1000000, BlockchainType.CARDANO, "addr1123", "stake1_test", "jbfw", fixedDate, fixedDate, true, fixedDate, false, null),
                    VerificationDto(2, 1000001, BlockchainType.POLYGON, "addr1124", "stake1kfekoef", "abkokob", fixedDate, fixedDate, true, fixedDate, false, null),
                    VerificationDto(3, 1000002, BlockchainType.CARDANO, "addr1125", "stake1_test2", "120910", fixedDate, fixedDate, true, fixedDate, true, 123),
                )
            )
        }

        val responseJson = loadJsonFromResource("sample-json/get-verifications-for-user.json")

        webClient.get()
            .uri("/externalaccounts/discord/$discordUserId/verifications")
            .exchange()
            .expectStatus().isOk
            .expectBody().json(responseJson)
    }
}
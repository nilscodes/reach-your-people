package io.vibrantnet.ryp.core.verification.controller

import io.mockk.InternalPlatformDsl.toStr
import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.verification.service.CheckApiService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(controllers = [CheckApiController::class, ApiExceptionHandler::class])
class CheckApiControllerIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        fun checkApiService() = mockk<CheckApiService>()
    }

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var checkApiService: CheckApiService

    @Test
    fun `verify returns a boolean when called`() {
        every { checkApiService.verify(any(), any(), any()) } answers {
            Mono.just(false)
        }

        webClient.get()
            .uri("/cip66/0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b/discord/111")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java).isEqualTo("false")
    }
}
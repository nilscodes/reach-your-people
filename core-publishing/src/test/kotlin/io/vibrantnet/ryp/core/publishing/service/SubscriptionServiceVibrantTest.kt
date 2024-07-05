package io.vibrantnet.ryp.core.publishing.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ryp.shared.model.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class SubscriptionServiceVibrantTest {
    @Test
    fun `getting a project works`() {
        val project = ProjectDto(
            id = 1,
            name = "HAZELpool",
            description = "A cat stakepool",
            logo = "ogo.png",
            category = ProjectCategory.sPO,
            url = "https://hazelpool.com",
            registrationTime = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC),
        )
        val projectPayload = configureObjectMapper().writeValueAsString(project)
        mockBackend.enqueue(
            MockResponse().setResponseCode(200).setBody(projectPayload).addHeader("Content-Type", "application/json")
        )
        val subscriptionService = SubscriptionServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build()
        )
        val result = subscriptionService.getProject(1)
        StepVerifier.create(result)
            .expectNext(project)
            .verifyComplete()
    }

    @Test
    fun `getting a list of linked external accounts for a user works`() {
        val linkedExternalAccounts = listOf(
            makeLinkedExternalAccountDto(12, 1212),
            makeLinkedExternalAccountDto(134, 5124),
        )
        val linkedExternalAccountsPayload = configureObjectMapper().writeValueAsString(linkedExternalAccounts)
        mockBackend.enqueue(
            MockResponse().setResponseCode(200).setBody(linkedExternalAccountsPayload)
                .addHeader("Content-Type", "application/json")
        )
        val subscriptionService = SubscriptionServiceVibrant(
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build()
        )
        val result = subscriptionService.getLinkedExternalAccounts(1)
        StepVerifier.create(result)
            .expectNext(linkedExternalAccounts[0])
            .expectNext(linkedExternalAccounts[1])
            .verifyComplete()
    }

    private fun configureObjectMapper(): ObjectMapper {
        return jacksonObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
    }

    companion object {
        lateinit var mockBackend: MockWebServer

        @JvmStatic
        @BeforeAll
        fun setUpClass() {
            mockBackend = MockWebServer()
            mockBackend.start()
        }

        @JvmStatic
        @AfterAll
        fun tearDownClass() {
            mockBackend.shutdown()
        }
    }
}

fun makeLinkedExternalAccountDto(
    linkId: Long,
    externalAccountId: Long,
    role: ExternalAccountRole = ExternalAccountRole.OWNER
) = LinkedExternalAccountDto(
    id = linkId,
    externalAccount = ExternalAccountDto(
        id = externalAccountId,
        referenceId = externalAccountId.toString(),
        type = "discord",
        registrationTime = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC),
    ),
    linkTime = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC),
    role = role,
    settings = setOf(ExternalAccountSetting.DEFAULT_FOR_NOTIFICATIONS),
)
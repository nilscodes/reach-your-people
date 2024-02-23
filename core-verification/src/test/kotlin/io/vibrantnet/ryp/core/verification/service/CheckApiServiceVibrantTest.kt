package io.vibrantnet.ryp.core.verification.service

import io.hazelnet.cardano.connect.data.token.PolicyId
import io.mockk.every
import io.mockk.mockk
import io.vibrantnet.ryp.core.verification.model.Cip66FileDto
import io.vibrantnet.ryp.core.verification.model.Cip66PayloadDto
import io.vibrantnet.ryp.core.verification.model.Cip66PolicyDto
import io.vibrantnet.ryp.core.verification.model.PAYLOAD
import io.vibrantnet.ryp.core.verification.persistence.Cip66Dao
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

val cip66Payload = Cip66PayloadDto(
    "1.0",
    mapOf(
        PolicyId("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b") to Cip66PolicyDto(
            "Ed25519VerificationKey2020",
            listOf(
                Cip66FileDto(
                    "ipfs://QmbV8ZcKvPVR5dC2DciQ6kbdfNPoMYkWhXq2FXoxbgDKro",
                    "CIP-0066_NMKR_IAMX",
                    "application/ld+json"
                )
            ),
            "https://github.com/IAMXID/did-method-iamx"
        )
    )
)

internal class CheckApiServiceVibrantTest {

    @Test
    fun `test getCip66InfoByPolicyId for empty CIP-0066 payload`() {
        val cip66Dao = mockk<Cip66Dao>()
        val checkApiServiceVibrant = CheckApiServiceVibrant(cip66Dao, mockk())
        every { cip66Dao.getCip66Payload("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b") } returns Cip66PayloadDto("1.0", emptyMap())
        val result = checkApiServiceVibrant.getCip66InfoByPolicyId("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b")
        StepVerifier.create(result)
            .expectNext(Cip66PayloadDto("1.0", emptyMap()))
            .verifyComplete()
    }

    @Test
    fun `verify CIP-0066 reference ID for a payload that has no content returns false`() {
        mockBackend.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val cip66Dao = mockk<Cip66Dao>()
        val checkApiServiceVibrant = CheckApiServiceVibrant(cip66Dao, WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build())
        every { cip66Dao.getCip66Payload("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b") } returns Cip66PayloadDto("1.0", emptyMap())
        val result = checkApiServiceVibrant.verify("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b", "service", "reference")
        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun `verify CIP-0066 reference ID for a payload that has no matching reference ID returns false`() {
        runVerificationWithBackendCall("111", false)
    }

    @Test
    fun `verify CIP-0066 reference ID for a payload that has a matching reference ID returns true`() {
        runVerificationWithBackendCall("222222222222222222", true)
    }

    private fun runVerificationWithBackendCall(referenceId: String, expected: Boolean) {
        mockBackend.enqueue(
            MockResponse().setResponseCode(200).setBody(PAYLOAD).addHeader("Content-Type", "application/ld+json")
        )
        val cip66Dao = mockk<Cip66Dao>()
        val checkApiServiceVibrant = CheckApiServiceVibrant(
            cip66Dao,
            WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build()
        )
        every { cip66Dao.getCip66Payload("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b") } returns cip66Payload
        val result = checkApiServiceVibrant.verify(
            "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
            "discord",
            referenceId
        )
        StepVerifier.create(result)
            .expectNext(expected)
            .verifyComplete()
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
package io.vibrantnet.ryp.core.verification.persistence

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

const val cip22Payload = """
    {
      "pool_id": "pool1pu5jlj4q9w9jlxeu370a3c9myx47md5j5m2str0naunn2q3lkdy",
      "hex": "0f292fcaa02b8b2f9b3c8f9fd8e0bb21abedb692a6d5058df3ef2735",
      "vrf_key": "0b5245f9934ec2151116fb8ec00f35fd00e0aa3b075c4ed12cce440f999d8233",
      "blocks_minted": 69,
      "blocks_epoch": 4,
      "live_stake": "6900000000",
      "live_size": 0.42,
      "live_saturation": 0.93,
      "live_delegators": 127,
      "active_stake": "4200000000",
      "active_size": 0.43,
      "declared_pledge": "5000000000",
      "live_pledge": "5000000001",
      "margin_cost": 0.05,
      "fixed_cost": "340000000",
      "reward_account": "stake1uxkptsa4lkr55jleztw43t37vgdn88l6ghclfwuxld2eykgpgvg3f",
      "owners": [
        "stake1u98nnlkvkk23vtvf9273uq7cph5ww6u2yq2389psuqet90sv4xv9v"
      ],
      "registration": [
        "9f83e5484f543e05b52e99988272a31da373f3aab4c064c76db96643a355d9dc",
        "7ce3b8c433bf401a190d58c8c483d8e3564dfd29ae8633c8b1b3e6c814403e95",
        "3e6e1200ce92977c3fe5996bd4d7d7e192bcb7e231bc762f9f240c76766535b9"
      ],
      "retirement": [
        "252f622976d39e646815db75a77289cf16df4ad2b287dd8e3a889ce14c13d1a8"
      ]
    }
"""

internal class Cip22DaoBlockfrostTest {
    @Test
    @OptIn(ExperimentalStdlibApi::class)
    fun `valid CIP-0022 payload from Blockfrost is successfully retrieved with mock server`() {
        mockBackend.enqueue(MockResponse().setResponseCode(200).setBody(cip22Payload).addHeader("Content-Type", "application/json"))
        val cip22Dao = Cip22DaoBlockfrost(WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build())
        val result = cip22Dao.getVrfVerificationKeyHashForPool("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b")
        StepVerifier.create(result)
            .expectNextMatches {
                it.toHexString() == "0b5245f9934ec2151116fb8ec00f35fd00e0aa3b075c4ed12cce440f999d8233"
            }
            .verifyComplete()
        val request = mockBackend.takeRequest()
        Assertions.assertEquals("/pools/0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b", request.path)
    }

    @Test
    fun `CIP-0022 payload not found in Blockfrost`() {
        mockBackend.enqueue(MockResponse().setResponseCode(404))
        val cip22Dao = Cip22DaoBlockfrost(WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build())
        val result = cip22Dao.getVrfVerificationKeyHashForPool("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b")
        StepVerifier.create(result)
            .expectErrorMatches { it is NoSuchElementException && it.message == "Pool info not found in Blockfrost for pool hash 0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b." }
            .verify()
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
package io.vibrantnet.ryp.core.verification.persistence

import io.ryp.cardano.model.stakepools.StakepoolDetailsDto
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

const val stakepoolDetailsPayload = """
{
    "pool_id": "pool1andsomemorecharacters",
    "hex": "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
    "url": "https://vibrantnet.io/metadata",
    "hash": "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
    "ticker": "VIBRN",
    "name": "Vibrant",  
    "description": "Vibrant Stake Pool",
    "homepage": "https://vibrantnet.io"
}    
"""

internal class StakepoolDaoBlockfrostTest {
    @Test
    fun `getting stake pool details via Blockfrost works if pool metadata is found`() {
        mockBackend.enqueue(MockResponse().setResponseCode(200).setBody(stakepoolDetailsPayload).addHeader("Content-Type", "application/json"))
        val stakepoolDao = StakepoolDaoBlockfrost(WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build())
        val result = stakepoolDao.getStakepoolDetails("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b")
        StepVerifier.create(result)
            .expectNext(
                StakepoolDetailsDto(
                poolHash = "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
                ticker = "VIBRN",
                name = "Vibrant",
                homepage = "https://vibrantnet.io",
                description = "Vibrant Stake Pool"
            )
            )
            .verifyComplete()
        val request = mockBackend.takeRequest()
        Assertions.assertEquals("/pools/0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b/metadata", request.path)
    }

    @Test
    fun `Stakepool details not found in Blockfrost`() {
        mockBackend.enqueue(MockResponse().setResponseCode(404))
        val stakepoolDao = StakepoolDaoBlockfrost(WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build())
        val result = stakepoolDao.getStakepoolDetails("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b")
        StepVerifier.create(result)
            .expectErrorMatches { it is NoSuchElementException && it.message == "Pool metadata not found in Blockfrost for pool hash 0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b." }
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
package io.vibrantnet.ryp.core.verification.persistence

import io.vibrantnet.ryp.core.verification.model.Cip66PayloadDto
import io.vibrantnet.ryp.core.verification.service.cip66Payload
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.util.concurrent.TimeUnit

const val assetPayload = """{
    "asset": "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
    "policy_id": "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b",
    "asset_name": null,
    "fingerprint": "asset1n5qj22cq3zldexfzyv39gst9rgvvf0339vaf7a",
    "quantity": "0",
    "initial_mint_tx_hash": "89235681afb41d7d94989ab3652e924de0d687015e634fb0a8f384a9fff0b0eb",
    "mint_or_burn_count": 2,
    "onchain_metadata": null,
    "onchain_metadata_standard": null,
    "onchain_metadata_extra": null,
    "metadata": null
}"""

const val txMetadataPayload = """[
    {
        "label": "725",
        "json_metadata": {
            "version": "1.0",
            "0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b": {
                "type": "Ed25519VerificationKey2020",
                "files": [
                    {
                        "src": "ipfs://QmbV8ZcKvPVR5dC2DciQ6kbdfNPoMYkWhXq2FXoxbgDKro",
                        "name": "CIP-0066_NMKR_IAMX",
                        "mediaType": "application/ld+json"
                    }
                ],
                "@context": "https://github.com/IAMXID/did-method-iamx"
            }
        }
    }
]"""

internal class Cip66DaoBlockfrostTest {

    @Test
    fun `valid CIP-0066 payload from Blockfrost is successfully retrieved with mock server`() {
        mockBackend.enqueue(MockResponse().setResponseCode(200).setBody(assetPayload).addHeader("Content-Type", "application/json"))
        mockBackend.enqueue(MockResponse().setResponseCode(200).setBody(txMetadataPayload).addHeader("Content-Type", "application/json"))
        val cip66Dao = Cip66DaoBlockfrost(WebClient.builder().baseUrl(String.format("http://localhost:%s", mockBackend.port)).build())
        val result = cip66Dao.getCip66Payload("0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b")
        StepVerifier.create(result)
            .expectNext(cip66Payload)
            .verifyComplete()
        val assetRequest = mockBackend.takeRequest(1, TimeUnit.SECONDS)
        assertEquals("/assets/0b80b4ac493eb53970282b9d19174d44892ca86a52e080fb013eed5b", assetRequest?.path)
        val metadataRequest = mockBackend.takeRequest(1, TimeUnit.SECONDS)
        assertEquals("/txs/89235681afb41d7d94989ab3652e924de0d687015e634fb0a8f384a9fff0b0eb/metadata", metadataRequest?.path)
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
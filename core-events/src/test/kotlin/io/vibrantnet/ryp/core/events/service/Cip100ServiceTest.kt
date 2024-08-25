package io.vibrantnet.ryp.core.events.service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

const val cip100Payload = """
    {
        "@context": {
            "@language": "en-us",
            "Proposal": "https://cips.cardano.org/cip/CIP-0100#Proposal",
            "hashAlgorithm": "https://cips.cardano.org/cip/CIP-0100#hashAlgorithm",
            "body": {
                "@id": "https://cips.cardano.org/cip/CIP-0100#body",
                "@context": {
                    "references": {
                        "@id": "https://cips.cardano.org/cip/CIP-0100#references",
                        "@container": "@set",
                        "@context": {
                            "GovernanceMetadata": "https://cips.cardano.org/cip/CIP-0100#GovernanceMetadataReference",
                            "Other": "https://cips.cardano.org/cip/CIP-0100#OtherReference",
                            "label": "https://cips.cardano.org/cip/CIP-0100#reference-label",
                            "uri": "https://cips.cardano.org/cip/CIP-0100#reference-uri"
                        }
                    },
                    "comment": "https://cips.cardano.org/cip/CIP-0100#comment",
                    "externalUpdates": {
                        "@id": "https://cips.cardano.org/cip/CIP-0100#externalUpdates",
                        "@context": {
                            "title": "https://cips.cardano.org/cip/CIP-0100#update-title",
                            "uri": "https://cips.cardano.org/cip/CIP-0100#update-uri"
                        }
                    }
                }
            },
            "authors": {
                "@id": "https://cips.cardano.org/cip/CIP-0100#authors",
                "@container": "@set",
                "@context": {
                    "did": "@id",
                    "name": "http://xmlns.com/foaf/0.1/name",
                    "witness": {
                        "@id": "https://cips.cardano.org/cip/CIP-0100#witness",
                        "@context": {
                            "witnessAlgorithm": "https://cips.cardano.org/cip/CIP-0100#witnessAlgorithm",
                            "publicKey": "https://cips.cardano.org/cip/CIP-0100#publicKey",
                            "signature": "https://cips.cardano.org/cip/CIP-0100#signature"
                        }
                    }
                }
            }
        },
        "hashAlgorithm": "blake2b-256",
        "authors": [
          { "name": "Ryan Williams", "witness": { "witnessAlgorithm": "ed25519", "publicKey": "2af88a521691cb317d5a8197024fdd1942c0bfab4760ecb81e74cf6b109b3138", "signature": "837e33683c9f03f66835d768b980251956ae589908a3e88f271b2d34806918dc99bb9d80010f67c25aa6a8a4253b1f2849aa191dacc63c881de1764c24cdf50c"}}
        ],
        "body": {
          "references": [
            { "@type": "other", "label": "Github", "uri": "https://github.com/Ryun1" }
          ],
          "comment": "This governance action aims to further Ryan's agenda!!",
          "externalUpdates": [
            { "title": "Twitter", "uri": "https://twitter.com/Ryun1_" }
          ]
        }
    }
"""

internal class Cip100ServiceTest {

    @Test
    fun `404 NOT found result for CIP-100 metadata yields error`() {
        mockBackend.enqueue(MockResponse().setResponseCode(404))
        val cip100Service = Cip100Service(
            WebClient.builder().build(),
            jacksonObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
        )
        val result = cip100Service.getCip100Document(String.format("http://localhost:%s", mockBackend.port))
        StepVerifier.create(result)
            .expectError()
            .verify()
    }

    @Test
    fun `Result with text plain type for CIP-100 metadata works if body is valid JSON`() {
        mockBackend.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(cip100Payload)
                .addHeader("Content-Type", "text/plain;charset=utf-8")
        )
        val cip100Service = Cip100Service(
            WebClient.builder().build(),
            jacksonObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
        )
        val result = cip100Service.getCip100Document(String.format("http://localhost:%s", mockBackend.port))
        StepVerifier.create(result)
            .expectNextMatches { it.body.comment == "This governance action aims to further Ryan's agenda!!" }
            .verifyComplete()
    }

    @Test
    fun `Result with applicatiaon json type fo CIP-100 metadata works if body is valid JSON`() {
        mockBackend.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(cip100Payload)
                .addHeader("Content-Type", "application/json;charset=utf-8")
        )
        val cip100Service = Cip100Service(
            WebClient.builder().build(),
            jacksonObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
        )
        val result = cip100Service.getCip100Document(String.format("http://localhost:%s", mockBackend.port))
        StepVerifier.create(result)
            .expectNextMatches { it.body.comment == "This governance action aims to further Ryan's agenda!!" }
            .verifyComplete()
    }

    @Test
    fun `Result with binary octet-stream type for CIP-100 metadata works if body is valid JSON`() {
        mockBackend.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(cip100Payload)
                .addHeader("Content-Type", "binary/octet-stream;charset=utf-8")
        )
        val cip100Service = Cip100Service(
            WebClient.builder().build(),
            jacksonObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
        )
        val result = cip100Service.getCip100Document(String.format("http://localhost:%s", mockBackend.port))
        StepVerifier.create(result)
            .expectNextMatches { it.body.comment == "This governance action aims to further Ryan's agenda!!" }
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
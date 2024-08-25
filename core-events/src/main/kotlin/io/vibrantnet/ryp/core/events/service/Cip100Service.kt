package io.vibrantnet.ryp.core.events.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.vibrantnet.ryp.core.events.model.cip100.Cip100Model
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

@Service
class Cip100Service(
    @Qualifier("cip100Client") private val cip100Client: WebClient,
    private val objectMapper: ObjectMapper,
) {

    /**
     * Fetches a CIP-100 document from the given URL, and deals with some of the various response types, like when hosting on a CDN that only streams
     * the content (hello AWS S3) and for regular responses.
     */
    fun getCip100Document(votingAnchorUrl: String) = cip100Client.get()
        .uri(votingAnchorUrl)
        .accept(
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_OCTET_STREAM,
            MediaType.TEXT_PLAIN,
            MediaType.valueOf("binary/octet-stream")
        )
        .exchangeToMono { response ->
            val contentType = response.headers().contentType().orElse(MediaType.APPLICATION_OCTET_STREAM)
            if (response.statusCode().isError) {
                return@exchangeToMono Mono.error(UnsupportedOperationException("Failed to fetch CIP-100 document: ${response.statusCode()}"))
            }
            when {
                contentType.includes(MediaType.APPLICATION_JSON) -> {
                    response.bodyToMono(Cip100Model::class.java)
                }
                contentType.includes(MediaType.TEXT_PLAIN) -> {
                    response.bodyToMono(String::class.java)
                        .map { objectMapper.readValue(it, Cip100Model::class.java) }
                }
                contentType.includes(MediaType.APPLICATION_OCTET_STREAM) || contentType.includes(
                    MediaType.valueOf(
                        "binary/octet-stream"
                    )
                ) -> {
                    response.bodyToMono(DataBuffer::class.java)
                        .map { buffer ->
                            val content = buffer.readableByteBuffers().asSequence()
                                .map { byteBuffer -> StandardCharsets.UTF_8.decode(byteBuffer).toString() }
                                .joinToString("")
                            objectMapper.readValue(content, Cip100Model::class.java)
                        }
                }
                else -> {
                    Mono.error(UnsupportedOperationException("Unsupported content type: $contentType"))
                }
            }
        }
}
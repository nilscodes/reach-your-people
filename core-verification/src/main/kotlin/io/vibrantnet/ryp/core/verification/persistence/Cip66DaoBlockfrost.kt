package io.vibrantnet.ryp.core.verification.persistence

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vibrantnet.ryp.core.verification.model.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Repository
@ConditionalOnProperty(prefix = "io.vibrantnet.ryp", name = ["type"], havingValue = "blockfrost")
class Cip66DaoBlockfrost(
    @Qualifier("blockfrostClient") private val blockfrostClient: WebClient,
) : Cip66Dao {
    override fun getCip66Payload(policyId: String): Mono<Cip66PayloadDto> {
        return blockfrostClient.get()
            .uri("/assets/$policyId")
            .retrieve()
            .onStatus({ status -> status == HttpStatus.NOT_FOUND }) { _ ->
                Mono.error(NoCip66DataAvailable("CIP-0066 metadata not found in Blockfrost for policy $policyId."))
            }
            .bodyToMono(PartialBlockfrostAssetInfo::class.java)
            .flatMap { assetInfo ->
                getCip66MetadataFromTransaction(policyId, assetInfo.initialMintTxHash)
            }
    }

    private fun getCip66MetadataFromTransaction(policyId: String, tx: String): Mono<Cip66PayloadDto> =
        blockfrostClient.get()
            .uri("/txs/${tx}/metadata")
            .retrieve()
            .bodyToMono(String::class.java)
            .handle { mintMetadataResponse, sink ->
                val objectMapper = jacksonObjectMapper().registerKotlinModule()
                val mintMetadata =
                    objectMapper.readValue(mintMetadataResponse, object : TypeReference<List<TxMetadataEntry>>() {})
                val cip66LabelMetadata = mintMetadata.find { it.label == CIP66_METADATA_KEY.toString() }
                if (cip66LabelMetadata == null) {
                    sink.error(IllegalStateException("CIP-0066 metadata not found for policy $policyId even though a mint transaction for the nameless token was found."))
                } else {
                    sink.next(makeCip66PayloadDtoFromMetadata(cip66LabelMetadata.jsonMetadata, objectMapper))
                }
            }
}
package io.vibrantnet.ryp.core.verification.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.hazelnet.cardano.connect.data.token.PolicyId
import io.vibrantnet.ryp.core.verification.persistence.DEFAULT_CIP66_VERSION

data class Cip66PayloadDto @JsonCreator constructor(
    @JsonProperty("version") val version: String,
    @JsonProperty("policies") val policies: Map<PolicyId, Cip66PolicyDto>
)

data class Cip66PolicyDto @JsonCreator constructor(
    @JsonProperty("type") val type: String,
    @JsonProperty("files") val files: List<Cip66FileDto>,
    @JsonProperty("@context") val context: String
)

data class Cip66FileDto @JsonCreator constructor(
    @JsonProperty("src") val src: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("mediaType") val mediaType: String,
)

fun makeCip66PayloadDtoFromMetadata(
    mintMetadata: Map<String, Any>,
    objectMapper: ObjectMapper
): Cip66PayloadDto {
    val policyData = mutableMapOf<PolicyId, Cip66PolicyDto>()
    var version = DEFAULT_CIP66_VERSION
    mintMetadata.entries.forEach {
        if (it.key == "version" && it.value is String) {
            version = it.value as String
        } else {
            try {
                val policyId = PolicyId(it.key)
                val policyInfo = objectMapper.convertValue(it.value, Cip66PolicyDto::class.java)
                policyData[policyId] = policyInfo
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return Cip66PayloadDto(
        version,
        policyData
    )
}

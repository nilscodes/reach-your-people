package io.vibrantnet.ryp.core.verification.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.hazelnet.cardano.connect.data.token.PolicyId

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


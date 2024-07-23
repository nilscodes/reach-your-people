package io.ryp.cardano.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.time.OffsetDateTime

data class StakepoolVerificationDto(
    @JsonProperty("nonce")
    @Pattern(regexp = "^[a-fA-F0-9]{64}$")
    val nonce: String,

    @JsonProperty("domain")
    @NotBlank
    val domain: String,

    @JsonProperty("poolHash")
    @Pattern(regexp = "^[a-fA-F0-9]{64}$")
    val poolHash: String,

    @JsonProperty("vrfVerificationKey")
    @Valid
    val vrfVerificationKey: VrfVerificationKey?,

    @JsonProperty("signature")
    @Pattern(regexp = "^[a-fA-F0-9]+$")
    val signature: String? = null,

    @JsonProperty("createTime")
    val createTime: OffsetDateTime,

    @JsonProperty("expirationTime")
    val expirationTime: OffsetDateTime,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class VrfVerificationKey(
    @JsonProperty("type")
    @NotBlank
    val type: String,

    @JsonProperty("description")
    @NotBlank
    val description: String,

    @JsonProperty("cborHex")
    @Pattern(regexp = "^5820[a-fA-F0-9]{64}$")
    val cborHex: String
)
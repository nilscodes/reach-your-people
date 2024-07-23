package io.ryp.shared.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import java.time.OffsetDateTime

data class ProjectDto @JsonCreator constructor(

    @JsonProperty("id", required = true)
    val id: Long? = null,

    @JsonProperty("name", required = true)
    val name: String,

    @JsonProperty("logo", required = true)
    val logo: String,

    @JsonProperty("url", required = true)
    val url: String,

    @JsonProperty("description", required = true)
    val description: String,

    @JsonProperty("category")
    val category: ProjectCategory,

    @JsonProperty("tags")
    val tags: Set<String> = emptySet(),

    @JsonProperty("registrationTime")
    val registrationTime: OffsetDateTime = OffsetDateTime.now(),

    @JsonProperty("verified")
    val verified: Boolean = false,

    @field:Valid
    @JsonProperty("policies")
    val policies: Set<PolicyDto> = emptySet(),

    @field:Valid
    @JsonProperty("stakepools")
    val stakepools: Set<StakepoolDto> = emptySet(),

    @JsonProperty("manuallyVerified")
    val manuallyVerified: OffsetDateTime? = null,
)

data class PolicyDto @JsonCreator constructor(
    @JsonProperty("name") val name: String,

    @field:Pattern(regexp="^[A-Fa-f0-9]{56}$")
    @JsonProperty("policyId") val policyId: String,

    @JsonProperty("manuallyVerified")
    val manuallyVerified: OffsetDateTime? = null,
)

data class StakepoolDto @JsonCreator constructor(
    @JsonProperty("poolHash")
    @field:Pattern(regexp="^[A-Fa-f0-9]{56}$")
    val poolHash: String,

    @JsonProperty("verificationNonce")
    val verificationNonce: String,
)

data class ProjectPartialDto @JsonCreator constructor(

    @JsonProperty("name", required = false)
    val name: String? = null,

    @JsonProperty("logo", required = false)
    val logo: String? = null,

    @JsonProperty("url", required = false)
    val url: String? = null,

    @JsonProperty("description", required = false)
    val description: String? = null,

    @JsonProperty("category", required = false)
    val category: ProjectCategory? = null,

    @JsonProperty("tags", required = false)
    val tags: Set<String>? = null,

    @field:Valid
    @JsonProperty("policies", required = false)
    val policies: Set<PolicyDto>? = null,

    @field:Valid
    @JsonProperty("stakepools", required = false)
    val stakepools: Set<StakepoolDto>? = null,

    @JsonProperty("manuallyVerified")
    val manuallyVerified: OffsetDateTime? = null,
)

enum class ProjectCategory(val value: String) {

    @JsonProperty("DeFi") deFi("DeFi"),
    @JsonProperty("NFT") nFT("NFT"),
    @JsonProperty("SPO") sPO("SPO"),
    @JsonProperty("dRep") dRep("dRep"),
    @JsonProperty("DAO") dAO("DAO"),
    @JsonProperty("Other") other("Other")
}

enum class ProjectRole {
    OWNER,
}
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

    @JsonProperty("manuallyVerified")
    val manuallyVerified: OffsetDateTime? = null,
)

data class PolicyDto @JsonCreator constructor(
    @JsonProperty("name") val name: String,

    @field:Pattern(regexp="^[A-Za-z0-9]{56}$")
    @JsonProperty("policyId") val policyId: String,

    @JsonProperty("manuallyVerified")
    val manuallyVerified: OffsetDateTime? = null,
)

data class ProjectPartialDto @JsonCreator constructor(

    @JsonProperty("name", required = false)
    val name: String?,

    @JsonProperty("logo", required = false)
    val logo: String?,

    @JsonProperty("url", required = false)
    val url: String?,

    @JsonProperty("description", required = false)
    val description: String?,

    @JsonProperty("category", required = false)
    val category: ProjectCategory?,

    @JsonProperty("tags", required = false)
    val tags: Set<String>?,

    @field:Valid
    @JsonProperty("policies", required = false)
    val policies: Set<PolicyDto>?,

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
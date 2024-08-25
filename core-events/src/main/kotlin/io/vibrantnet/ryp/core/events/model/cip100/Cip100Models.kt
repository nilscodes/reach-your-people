package io.vibrantnet.ryp.core.events.model.cip100

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Cip100Model(
    val body: Cip100Body
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Cip100Body(
    val comment: String? = null,
    val abstract: String? = null,
    val motivation: String? = null,
    val rationale: String? = null,
    val title: String? = null,
)

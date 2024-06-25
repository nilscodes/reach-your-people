package io.vibrantnet.ryp.core.publishing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude

data class Statistics @JsonCreator constructor(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val sent: Map<String, Long> = emptyMap(),

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val uniqueAccounts: Long? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val explicitSubscribers: Long? = null,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val delivered: Map<String, Long> = emptyMap(),

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val failures: Map<String, Long> = emptyMap(),

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val views: Map<String, Long> = emptyMap(),
)
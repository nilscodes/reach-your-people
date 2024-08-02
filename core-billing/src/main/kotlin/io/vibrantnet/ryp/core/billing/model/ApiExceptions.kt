package io.vibrantnet.ryp.core.billing.model

open class ApiException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)
package io.vibrantnet.ryp.core.verification.model

open class ApiException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class NoCip66DataAvailable(message: String? = null, cause: Throwable? = null) : ApiException(message, cause)
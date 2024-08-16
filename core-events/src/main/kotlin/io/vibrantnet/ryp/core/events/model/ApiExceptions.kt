package io.vibrantnet.ryp.core.events.model

open class ApiException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class NoCip66DataAvailable(message: String? = null, cause: Throwable? = null) : ApiException(message, cause)

class InvalidCip22Verification(message: String? = null, cause: Throwable? = null) : ApiException(message, cause)

class ExpiredCip22Verification(message: String? = null, cause: Throwable? = null) : ApiException(message, cause)
package io.vibrantnet.ryp.core.subscription.model

open class ApiException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class ExternalAccountAlreadyLinkedException(message: String? = null, cause: Throwable? = null) : ApiException(message, cause)
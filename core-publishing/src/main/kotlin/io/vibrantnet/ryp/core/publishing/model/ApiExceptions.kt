package io.vibrantnet.ryp.core.publishing.model

open class ApiException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class UserNotAuthorizedToPublishException(message: String? = null, cause: Throwable? = null) : ApiException(message, cause)
package io.vibrantnet.ryp.core.points.model

open class ApiException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class DuplicatePointsClaimException(message: String? = null, cause: Throwable? = null) : ApiException(message, cause)
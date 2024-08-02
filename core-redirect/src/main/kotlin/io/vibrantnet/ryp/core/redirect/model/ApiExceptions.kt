package io.vibrantnet.ryp.core.redirect.model

open class ApiException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class DuplicateShortcodeException(message: String? = null, cause: Throwable? = null) : ApiException(message, cause)
package io.vibrantnet.ryp.core.subscription.controller

import io.hazelnet.shared.data.ApiErrorResponse
import io.vibrantnet.ryp.core.subscription.model.ExternalAccountAlreadyLinkedException
import io.vibrantnet.ryp.core.subscription.model.IncompatibleExternalAccountChangeException
import io.vibrantnet.ryp.core.subscription.model.PermissionDeniedException
import mu.KotlinLogging
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
@Order(-10)
class ApiExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun processObjectNotFoundError(ex: NoSuchElementException): ApiErrorResponse {
        logger.info { ex }
        return ApiErrorResponse(ex.message ?: "", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ExternalAccountAlreadyLinkedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun processExternalAccountAlreadyLinkedException(ex: ExternalAccountAlreadyLinkedException): ApiErrorResponse {
        logger.info { ex }
        return ApiErrorResponse(ex.message ?: "", HttpStatus.CONFLICT)
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun processIllegalArgumentException(ex: IllegalArgumentException): ApiErrorResponse {
        logger.info { ex }
        return ApiErrorResponse(ex.message ?: "", HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun processIncompatibleExternalAccountChangeException(ex: IncompatibleExternalAccountChangeException): ApiErrorResponse {
        logger.info { ex }
        return ApiErrorResponse(ex.message ?: "", HttpStatus.CONFLICT)
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    fun processPermissionDeniedException(ex: PermissionDeniedException): ApiErrorResponse {
        logger.info { ex }
        return ApiErrorResponse(ex.message ?: "", HttpStatus.FORBIDDEN)
    }
}

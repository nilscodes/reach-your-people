package io.vibrantnet.ryp.core.publishing.controller

import io.hazelnet.shared.data.ApiErrorResponse
import io.vibrantnet.ryp.core.publishing.model.UserNotAuthorizedToPublishException
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

    @ExceptionHandler(UserNotAuthorizedToPublishException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    fun processUserNotAuthorizedToPublishException(ex: UserNotAuthorizedToPublishException): ApiErrorResponse {
        logger.info { ex }
        return ApiErrorResponse(ex.message ?: "", HttpStatus.FORBIDDEN)
    }
}

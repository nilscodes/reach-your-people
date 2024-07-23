package io.vibrantnet.ryp.core.verification.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.hazelnet.shared.data.ApiErrorResponse
import io.vibrantnet.ryp.core.verification.model.ExpiredCip22Verification
import io.vibrantnet.ryp.core.verification.model.InvalidCip22Verification
import io.vibrantnet.ryp.core.verification.model.NoCip66DataAvailable
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

    @ExceptionHandler(NoCip66DataAvailable::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun processNoCip66DataAvailableError(ex: NoCip66DataAvailable): ApiErrorResponse {
        logger.info { ex }
        return ApiErrorResponse(ex.message ?: "", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InvalidCip22Verification::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    fun processInvalidCip22VerificationError(ex: InvalidCip22Verification): ApiErrorResponse {
        logger.info { ex }
        return ApiErrorResponse(ex.message ?: "", HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ExpiredCip22Verification::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun processExpiredCip22VerificationError(ex: ExpiredCip22Verification): ApiErrorResponse {
        logger.info { ex }
        return ApiErrorResponse(ex.message ?: "", HttpStatus.NOT_FOUND)
    }

}

package io.vibrantnet.ryp.core.verification.controller

import io.hazelnet.shared.data.ApiErrorResponse
import io.vibrantnet.ryp.core.verification.model.ExpiredCip22Verification
import io.vibrantnet.ryp.core.verification.model.InvalidCip22Verification
import io.vibrantnet.ryp.core.verification.model.NoCip66DataAvailable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

internal class ApiExceptionHandlerTest {
    @Test
    fun `processObjectNotFoundError works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = NoSuchElementException("test")
        val result = apiExceptionHandler.processObjectNotFoundError(exception)
        Assertions.assertEquals(ApiErrorResponse("test", HttpStatus.NOT_FOUND), result)
    }

    @Test
    fun `processObjectNotFoundError works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = NoSuchElementException()
        val result = apiExceptionHandler.processObjectNotFoundError(exception)
        Assertions.assertEquals(ApiErrorResponse("", HttpStatus.NOT_FOUND), result)
    }

    @Test
    fun `processNoCip66DataAvailableError works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = NoCip66DataAvailable("test")
        val result = apiExceptionHandler.processNoCip66DataAvailableError(exception)
        Assertions.assertEquals(ApiErrorResponse("test", HttpStatus.NOT_FOUND), result)
    }

    @Test
    fun `processNoCip66DataAvailableError works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = NoCip66DataAvailable()
        val result = apiExceptionHandler.processNoCip66DataAvailableError(exception)
        Assertions.assertEquals(ApiErrorResponse("", HttpStatus.NOT_FOUND), result)
    }

    @Test
    fun `processInvalidCip22VerificationError works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = InvalidCip22Verification("test")
        val result = apiExceptionHandler.processInvalidCip22VerificationError(exception)
        Assertions.assertEquals(ApiErrorResponse("test", HttpStatus.CONFLICT), result)
    }

    @Test
    fun `processInvalidCip22VerificationError works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = InvalidCip22Verification()
        val result = apiExceptionHandler.processInvalidCip22VerificationError(exception)
        Assertions.assertEquals(ApiErrorResponse("", HttpStatus.CONFLICT), result)
    }

    @Test
    fun `processExpiredCip22VerificationError works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = ExpiredCip22Verification("test")
        val result = apiExceptionHandler.processExpiredCip22VerificationError(exception)
        Assertions.assertEquals(ApiErrorResponse("test", HttpStatus.NOT_FOUND), result)
    }

    @Test
    fun `processExpiredCip22VerificationError works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = ExpiredCip22Verification()
        val result = apiExceptionHandler.processExpiredCip22VerificationError(exception)
        Assertions.assertEquals(ApiErrorResponse("", HttpStatus.NOT_FOUND), result)
    }
}
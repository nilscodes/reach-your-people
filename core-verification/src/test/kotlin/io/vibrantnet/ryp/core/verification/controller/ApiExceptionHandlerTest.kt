package io.vibrantnet.ryp.core.verification.controller

import io.hazelnet.shared.data.ApiErrorResponse
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
}
package io.vibrantnet.ryp.core.publishing.controller

import io.hazelnet.shared.data.ApiErrorResponse
import io.vibrantnet.ryp.core.publishing.model.UserNotAuthorizedToPublishException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

internal class ApiExceptionHandlerTest {
    @Test
    fun `processObjectNotFoundError works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = NoSuchElementException("test")
        val result = apiExceptionHandler.processObjectNotFoundError(exception)
        assertEquals(ApiErrorResponse("test", HttpStatus.NOT_FOUND), result)
    }

    @Test
    fun `processObjectNotFoundError works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = NoSuchElementException()
        val result = apiExceptionHandler.processObjectNotFoundError(exception)
        assertEquals(ApiErrorResponse("", HttpStatus.NOT_FOUND), result)
    }

    @Test
    fun `processUserNotAuthorizedToPublishException works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = UserNotAuthorizedToPublishException("test")
        val result = apiExceptionHandler.processUserNotAuthorizedToPublishException(exception)
        assertEquals(ApiErrorResponse("test", HttpStatus.FORBIDDEN), result)
    }

    @Test
    fun `processUserNotAuthorizedToPublishException works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = UserNotAuthorizedToPublishException(null)
        val result = apiExceptionHandler.processUserNotAuthorizedToPublishException(exception)
        assertEquals(ApiErrorResponse("", HttpStatus.FORBIDDEN), result)
    }
}

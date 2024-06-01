package io.vibrantnet.ryp.core.subscription.controller

import io.hazelnet.shared.data.ApiErrorResponse
import io.vibrantnet.ryp.core.subscription.model.ExternalAccountAlreadyLinkedException
import io.vibrantnet.ryp.core.subscription.model.IncompatibleExternalAccountChangeException
import io.vibrantnet.ryp.core.subscription.model.PermissionDeniedException
import org.junit.jupiter.api.Assertions.*
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
    fun `processExternalAccountAlreadyLinkedException works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = ExternalAccountAlreadyLinkedException("test")
        val result = apiExceptionHandler.processExternalAccountAlreadyLinkedException(exception)
        assertEquals(ApiErrorResponse("test", HttpStatus.CONFLICT), result)
    }

    @Test
    fun `processExternalAccountAlreadyLinkedException works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = ExternalAccountAlreadyLinkedException(null)
        val result = apiExceptionHandler.processExternalAccountAlreadyLinkedException(exception)
        assertEquals(ApiErrorResponse("", HttpStatus.CONFLICT), result)
    }

    @Test
    fun `processIllegalArgumentException works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = IllegalArgumentException("test")
        val result = apiExceptionHandler.processIllegalArgumentException(exception)
        assertEquals(ApiErrorResponse("test", HttpStatus.BAD_REQUEST), result)
    }

    @Test
    fun `processIllegalArgumentException works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = IllegalArgumentException(null as String?)
        val result = apiExceptionHandler.processIllegalArgumentException(exception)
        assertEquals(ApiErrorResponse("", HttpStatus.BAD_REQUEST), result)
    }

    @Test
    fun `processIncompatibleExternalAccountChangeException works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = IncompatibleExternalAccountChangeException("test")
        val result = apiExceptionHandler.processIncompatibleExternalAccountChangeException(exception)
        assertEquals(ApiErrorResponse("test", HttpStatus.CONFLICT), result)
    }

    @Test
    fun `processIncompatibleExternalAccountChangeException works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = IncompatibleExternalAccountChangeException(null as String?)
        val result = apiExceptionHandler.processIncompatibleExternalAccountChangeException(exception)
        assertEquals(ApiErrorResponse("", HttpStatus.CONFLICT), result)
    }

    @Test
    fun `processPermissionDeniedException works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = PermissionDeniedException("test")
        val result = apiExceptionHandler.processPermissionDeniedException(exception)
        assertEquals(ApiErrorResponse("test", HttpStatus.FORBIDDEN), result)
    }

    @Test
    fun `processPermissionDeniedException works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = PermissionDeniedException(null as String?)
        val result = apiExceptionHandler.processPermissionDeniedException(exception)
        assertEquals(ApiErrorResponse("", HttpStatus.FORBIDDEN), result)
    }
}

package io.vibrantnet.ryp.core.redirect.controller

import io.hazelnet.shared.data.ApiErrorResponse
import io.vibrantnet.ryp.core.redirect.model.DuplicateShortcodeException
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
    fun `processDuplicateShortcodeException works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = DuplicateShortcodeException("test")
        val result = apiExceptionHandler.processDuplicateShortcodeException(exception)
        assertEquals(ApiErrorResponse("test", HttpStatus.CONFLICT), result)
    }

    @Test
    fun `processDuplicateShortcodeException works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = DuplicateShortcodeException()
        val result = apiExceptionHandler.processDuplicateShortcodeException(exception)
        assertEquals(ApiErrorResponse("", HttpStatus.CONFLICT), result)
    }
}

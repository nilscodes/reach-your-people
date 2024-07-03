package io.vibrantnet.ryp.core.points.controller

import io.hazelnet.shared.data.ApiErrorResponse
import io.vibrantnet.ryp.core.points.model.DuplicatePointsClaimException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ApiExceptionHandlerTest {
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
    fun `processDuplicatePointsClaimError works with message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = DuplicatePointsClaimException("test")
        val result = apiExceptionHandler.processDuplicatePointsClaimError(exception)
        assertEquals(ApiErrorResponse("test", HttpStatus.CONFLICT), result)
    }

    @Test
    fun `processDuplicatePointsClaimError works without message`() {
        val apiExceptionHandler = ApiExceptionHandler()
        val exception = DuplicatePointsClaimException()
        val result = apiExceptionHandler.processDuplicatePointsClaimError(exception)
        assertEquals(ApiErrorResponse("", HttpStatus.CONFLICT), result)
    }
}
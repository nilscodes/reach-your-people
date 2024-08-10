package io.hazelnet.shared.data

import io.ryp.core.createDefaultObjectMapper
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.http.HttpStatus

internal class ApiErrorResponseTest {
    @Test
    fun serializationTest() {
        val dto = ApiErrorResponse(
            message = "message",
            httpStatus = HttpStatus.ACCEPTED,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "messages": [
                    { 
                      "message": "message"
                    }
                ],
                "httpStatus": "ACCEPTED",
                "httpStatusCode": 202
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
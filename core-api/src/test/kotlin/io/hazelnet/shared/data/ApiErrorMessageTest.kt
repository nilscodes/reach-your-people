package io.hazelnet.shared.data

import io.ryp.core.createDefaultObjectMapper
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class ApiErrorMessageTest {
    @Test
    fun serializationTest() {
        val dto = ApiErrorMessage(
            message = "message",
            sourceField = "sourceField",
            additionalData = mapOf("key" to "value")
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "message": "message",
                "sourceField": "sourceField",
                "additionalData": {
                    "key": "value"
                }
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
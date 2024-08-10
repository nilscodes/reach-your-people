package io.ryp.cardano.model

import io.ryp.core.createDefaultObjectMapper
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class TxOutputSummaryDtoTest {
    @Test
    fun serializationTest() {
        val dto = TxOutputSummaryDto(
            address = "address",
            lovelace = 17000000,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "address": "address",
                "lovelace": 17000000    
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
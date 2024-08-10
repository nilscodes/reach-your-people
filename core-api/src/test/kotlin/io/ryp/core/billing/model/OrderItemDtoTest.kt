package io.ryp.core.billing.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class OrderItemDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(OrderItemDto::class.java).verify()
    }

    @Test
    fun serializationTest() {
        val dto = OrderItemDto("stuff", 17)
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                    "type": "stuff",
                    "amount": 17
                  }
                
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
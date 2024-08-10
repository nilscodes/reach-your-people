package io.ryp.core.billing.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

internal class OrderDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(OrderDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val dto = OrderDto(
            id = 1313,
            items = listOf(
                OrderItemDto("stuff", 17)
            )
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "id": 1313,
                "items": [
                  {
                    "type": "stuff",
                    "amount": 17
                  }
                ]
              }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
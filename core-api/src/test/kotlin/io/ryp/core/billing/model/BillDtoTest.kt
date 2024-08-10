package io.ryp.core.billing.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime

internal class BillDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(BillDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val now = OffsetDateTime.parse("2021-08-01T12:00:00Z")
        val dto = BillDto(
            accountId = 12,
            createTime = OffsetDateTime.parse("2021-01-01T00:00:00Z"),
            id = 209,
            channel = "cardano",
            currency = Currency.LOVELACE_ADA,
            order = OrderDto(
                id = 1313,
                items = listOf(
                    OrderItemDto("stuff", 17)
                )
            ),
            amountRequested = 25000000L,
            amountReceived = 120L,
            paymentProcessedTime = now,
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
              "accountId": 12,
              "createTime": "2021-01-01T00:00:00Z",
              "id": 209,
              "channel": "cardano",
              "currencyId": 1,
              "order": {
                "id": 1313,
                "items": [
                  {
                    "type": "stuff",
                    "amount": 17
                  }
                ]
              },
              "amountRequested": 25000000,
              "amountReceived": 120,
              "paymentProcessedTime": "2021-08-01T12:00:00Z"
            }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
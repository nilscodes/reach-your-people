package io.ryp.cardano.model

import io.ryp.core.createDefaultObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.time.OffsetDateTime

internal class TransactionSummaryDtoTest {
    @Test
    fun testEqualsAndHashCode() {
        EqualsVerifier.forClass(TransactionSummaryDto::class.java)
            .verify()
    }

    @Test
    fun serializationTest() {
        val now = OffsetDateTime.parse("2021-08-01T12:00:00Z")
        val dto = TransactionSummaryDto(
            transactionHash = "transactionHash",
            transactionTime = now,
            outputs = listOf(
                TxOutputSummaryDto(
                    address = "address",
                    lovelace = 17000000,
                )
            )
        )
        val objectMapper = createDefaultObjectMapper()

        JSONAssert.assertEquals(
            """
            {
                "transactionHash": "transactionHash",
                "transactionTime": "2021-08-01T12:00:00Z",
                "outputs": [
                  {
                    "address": "address",
                    "lovelace": 17000000
                  }
                ]
              }
            """.trimIndent(), objectMapper.writeValueAsString(dto), false
        )

    }
}
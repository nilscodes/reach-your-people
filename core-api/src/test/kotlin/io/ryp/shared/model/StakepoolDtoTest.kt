package io.ryp.shared.model

import nl.jqno.equalsverifier.EqualsVerifier
import org.junit.jupiter.api.Test

internal class StakepoolDtoTest {
   @Test
   fun testEqualsAndHashCode() {
      EqualsVerifier.forClass(StakepoolDto::class.java)
         .verify()
   }
}
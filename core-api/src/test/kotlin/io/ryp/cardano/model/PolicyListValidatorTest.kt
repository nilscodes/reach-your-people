package io.ryp.cardano.model

import io.mockk.mockk
import jakarta.validation.ConstraintValidatorContext
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PolicyListValidatorTest {

    private val validator = PolicyListValidator()
    private val context = mockk<ConstraintValidatorContext>(relaxed = true)

    @Test
    fun `should return true for null list`() {
        val result = validator.isValid(null, context)
        assertTrue(result, "Validator should return true for null list")
    }

    @Test
    fun `should return true for valid policy list`() {
        val validPolicies = listOf(
            "df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058",
            "4523C5E21D409B81C95B45B0AEA275B8EA1406E6CAFEA5583B9F8A5F"
        )
        val result = validator.isValid(validPolicies, context)
        assertTrue(result, "Validator should return true for a list of valid policies")
    }

    @Test
    fun `should return false for invalid policy list`() {
        val invalidPolicies = listOf(
            "INVALID_POLICY_STRING",
            "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f"
        )
        val result = validator.isValid(invalidPolicies, context)
        assertFalse(result, "Validator should return false for a list containing invalid policies")
    }

    @Test
    fun `should return true for an empty list`() {
        val emptyPolicies = listOf<String>()
        val result = validator.isValid(emptyPolicies, context)
        assertTrue(result, "Validator should return true for an empty list")
    }
}

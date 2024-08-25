package io.ryp.cardano.model.stakepools

import io.mockk.mockk
import jakarta.validation.ConstraintValidatorContext
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class StakepoolHashValidatorTest {

    private val validator = StakepoolHashValidator()
    private val context = mockk<ConstraintValidatorContext>(relaxed = true)

    @Test
    fun `should return true for null list`() {
        val result = validator.isValid(null, context)
        assertTrue(result, "Validator should return true for null list")
    }

    @Test
    fun `should return true for valid stake pool hash list`() {
        val validStakePoolHashes = listOf(
            "df6fe8ac7a40d0be2278d7d0048bc01877533d48852d5eddf2724058",
            "4523C5E21D409B81C95B45B0AEA275B8EA1406E6CAFEA5583B9F8A5F"
        )
        val result = validator.isValid(validStakePoolHashes, context)
        assertTrue(result, "Validator should return true for a list of valid stake pool hashes")
    }

    @Test
    fun `should return false for invalid stake pool hash list`() {
        val invalidStakePoolHashes = listOf(
            "INVALID_POOL_HASH_STRING",
            "4523c5e21d409b81c95b45b0aea275b8ea1406e6cafea5583b9f8a5f"
        )
        val result = validator.isValid(invalidStakePoolHashes, context)
        assertFalse(result, "Validator should return false for a list containing invalid stake pool hashes")
    }

    @Test
    fun `should return true for an empty list`() {
        val emptyStakePoolHashes = listOf<String>()
        val result = validator.isValid(emptyStakePoolHashes, context)
        assertTrue(result, "Validator should return true for an empty list")
    }
}

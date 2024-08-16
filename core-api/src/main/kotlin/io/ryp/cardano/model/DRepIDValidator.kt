package io.ryp.cardano.model

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [DRepIDValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidDRepIDList(
    val message: String = "Invalid dRep ID format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class DRepIDValidator : ConstraintValidator<ValidDRepIDList, List<String>?> {
    private val pattern = "^[A-Za-z0-9]{56}$".toRegex()

    override fun isValid(value: List<String>?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            return true
        }
        return value.all { it.matches(pattern) }
    }
}

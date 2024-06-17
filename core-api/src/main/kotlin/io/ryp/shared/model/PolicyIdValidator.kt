package io.ryp.shared.model

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [PolicyListValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidPolicyList(
    val message: String = "Invalid policy format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class PolicyListValidator : ConstraintValidator<ValidPolicyList, List<String>?> {
    private val pattern = "^[A-Za-z0-9]{56}$".toRegex()

    override fun isValid(value: List<String>?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            return true
        }
        return value.all { it.matches(pattern) }
    }
}

package com.gradle.develocity.assignment.common.validation

abstract class Validator<T> {
    fun validate(item: T): ValidationResult<T> {
        val errors: MutableList<ValidationError> = mutableListOf()

        validationRules(item, errors)

        return ValidationResult(item, errors)
    }

    internal abstract fun validationRules(item: T, errors: MutableList<ValidationError>)
}

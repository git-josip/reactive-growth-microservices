package com.gradle.develocity.assignment.common.validation

import com.gradle.develocity.assignment.common.exception.ValidationException

data class ValidationResult<T>(val validatedItem: T, val errors: List<ValidationError>) {
    fun hasErrors() = errors.isNotEmpty()
    fun failOnError(): Unit = hasErrors().let {
        when (it) {
            true -> throw ValidationException(errors = errors)
            false -> Unit
        }
    }
}

package com.reactive.review.common.validation

import com.reactive.review.common.exception.ValidationException

data class ValidationResult<T>(val validatedItem: T, val errors: List<com.reactive.review.common.validation.ValidationError>) {
    fun hasErrors() = errors.isNotEmpty()
    fun failOnError(): Unit = hasErrors().let {
        when (it) {
            true -> throw ValidationException(errors = errors)
            false -> Unit
        }
    }
}

fun com.reactive.review.common.validation.ValidationResult<*>.ifNoErrors(block: () -> Unit) {
    if (!this.hasErrors()) {
        block()
    }
}

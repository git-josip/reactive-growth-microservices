package com.reactive.productcomposite.common.validation

import com.reactive.productcomposite.common.exception.ValidationException

data class ValidationResult<T>(val validatedItem: T, val errors: List<com.reactive.productcomposite.common.validation.ValidationError>) {
    fun hasErrors() = errors.isNotEmpty()
    fun failOnError(): Unit = hasErrors().let {
        when (it) {
            true -> throw ValidationException(errors = errors)
            false -> Unit
        }
    }
}

fun com.reactive.productcomposite.common.validation.ValidationResult<*>.ifNoErrors(block: () -> Unit) {
    if (!this.hasErrors()) {
        block()
    }
}

package com.reactive.order.common.validation

import com.reactive.order.common.exception.ValidationException

data class ValidationResult<T>(val validatedItem: T, val errors: List<ValidationError>) {
    fun hasErrors() = errors.isNotEmpty()
    fun failOnError(): Unit = hasErrors().let {
        when (it) {
            true -> throw ValidationException(errors = errors)
            false -> Unit
        }
    }
}

fun ValidationResult<*>.ifNoErrors(block: () -> Unit) {
    if (!this.hasErrors()) {
        block()
    }
}

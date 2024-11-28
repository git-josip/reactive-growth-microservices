package com.reactive.apigateway.common.validation

import com.reactive.apigateway.common.exception.ValidationException

data class ValidationResult<T>(val validatedItem: T, val errors: List<ValidationError>) {
    fun hasErrors() = errors.isNotEmpty()
    fun failOnError(): Unit = hasErrors().let {
        when (it) {
            true -> throw com.reactive.apigateway.common.exception.ValidationException(errors = errors)
            false -> Unit
        }
    }
}

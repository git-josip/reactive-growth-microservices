package com.reactive.order.common.exception

import com.reactive.order.common.validation.ValidationError

data class ValidationException(
    val errors: List<ValidationError> = listOf(),
    override val message: String = "Object failed validation.\nErrors: $errors"
) : RuntimeException(message)
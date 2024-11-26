package com.reactive.productcomposite.common.exception

import com.reactive.productcomposite.common.validation.ValidationError

data class ValidationException(
    val errors: List<ValidationError> = listOf(),
    override val message: String = "Object failed validation.\nErrors: $errors"
) : RuntimeException(message)
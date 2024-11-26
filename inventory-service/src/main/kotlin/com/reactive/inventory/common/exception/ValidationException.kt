package com.reactive.inventory.common.exception

import com.reactive.inventory.common.validation.ValidationError

data class ValidationException(
    val errors: List<ValidationError> = listOf(),
    override val message: String = "Object failed validation.\nErrors: $errors"
) : RuntimeException(message)
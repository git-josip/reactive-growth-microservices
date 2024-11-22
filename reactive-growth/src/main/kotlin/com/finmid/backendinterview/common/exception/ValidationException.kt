package com.finmid.backendinterview.common.exception

import com.finmid.backendinterview.common.validation.ValidationError

data class ValidationException(
    val errors: List<ValidationError> = listOf(),
    override val message: String = "Object failed validation.\nErrors: $errors"
) : RuntimeException(message)
package com.reactive.apigateway.common.exception

import com.reactive.apigateway.common.validation.ValidationError

class ValidationException(
    override val message: String = "Validation Exception",
    val errors: List<ValidationError>
) : RuntimeException(message)
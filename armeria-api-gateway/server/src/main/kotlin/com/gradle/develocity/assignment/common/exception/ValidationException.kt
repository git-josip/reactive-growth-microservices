package com.gradle.develocity.assignment.common.exception

import com.gradle.develocity.assignment.common.validation.ValidationError

class ValidationException(
    override val message: String = "Validation Exception",
    val errors: List<ValidationError>
) : RuntimeException(message)
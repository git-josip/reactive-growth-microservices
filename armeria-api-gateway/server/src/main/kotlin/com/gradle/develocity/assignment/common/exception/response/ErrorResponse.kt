package com.gradle.develocity.assignment.common.exception.response

import com.gradle.develocity.assignment.common.validation.ValidationError

data class ErrorResponse(
    val success: Boolean = false,
    val errorMsg: String,
    val errors: List<ValidationError> = listOf()
)
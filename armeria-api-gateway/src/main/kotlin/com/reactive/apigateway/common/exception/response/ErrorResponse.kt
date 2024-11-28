package com.reactive.apigateway.common.exception.response

import com.reactive.apigateway.common.validation.ValidationError

data class ErrorResponse(
    val success: Boolean = false,
    val errorMsg: String,
    val errors: List<ValidationError> = listOf()
)
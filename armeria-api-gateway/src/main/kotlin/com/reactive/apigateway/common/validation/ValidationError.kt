package com.reactive.apigateway.common.validation

data class ValidationError(
    val field: String,
    val message: String
)

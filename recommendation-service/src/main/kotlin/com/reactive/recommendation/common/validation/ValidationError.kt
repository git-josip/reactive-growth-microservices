package com.reactive.recommendation.common.validation

data class ValidationError(
    val field: String,
    val message: String
)

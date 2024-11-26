package com.reactive.review.common.validation

data class ValidationError(
    val field: String,
    val message: String
)

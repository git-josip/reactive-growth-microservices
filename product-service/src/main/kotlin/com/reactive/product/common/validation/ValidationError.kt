package com.reactive.product.common.validation

data class ValidationError(
    val field: String,
    val message: String
)

package com.reactive.order.common.validation

data class ValidationError(
    val field: String,
    val message: String
)

package com.reactive.inventory.common.validation

data class ValidationError(
    val field: String,
    val message: String
)

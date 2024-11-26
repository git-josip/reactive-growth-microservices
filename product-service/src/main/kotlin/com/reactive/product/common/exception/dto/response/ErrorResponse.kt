package com.reactive.product.common.exception.dto.response

import com.reactive.product.common.validation.ValidationError

data class ErrorResponse(
    val errorMsg: String,
    val errors: List<ValidationError> = listOf()
)
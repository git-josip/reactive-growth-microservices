package com.reactive.order.common.exception.dto.response

import com.reactive.order.common.validation.ValidationError

data class ErrorResponse(
    val errorMsg: String,
    val errors: List<ValidationError> = listOf()
)
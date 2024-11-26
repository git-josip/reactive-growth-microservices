package com.reactive.productcomposite.common.exception.dto.response

import com.reactive.productcomposite.common.validation.ValidationError

data class ErrorResponse(
    val errorMsg: String,
    val errors: List<ValidationError> = listOf()
)
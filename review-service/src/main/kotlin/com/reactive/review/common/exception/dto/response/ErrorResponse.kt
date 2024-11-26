package com.reactive.review.common.exception.dto.response

import com.reactive.review.common.validation.ValidationError

data class ErrorResponse(
    val errorMsg: String,
    val errors: List<ValidationError> = listOf()
)
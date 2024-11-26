package com.reactive.recommendation.common.exception.dto.response

import com.reactive.recommendation.common.validation.ValidationError

data class ErrorResponse(
    val errorMsg: String,
    val errors: List<ValidationError> = listOf()
)
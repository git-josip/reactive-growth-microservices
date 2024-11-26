package com.reactive.inventory.common.exception.dto.response

import com.reactive.inventory.common.validation.ValidationError

data class ErrorResponse(
    val errorMsg: String,
    val errors: List<ValidationError> = listOf()
)
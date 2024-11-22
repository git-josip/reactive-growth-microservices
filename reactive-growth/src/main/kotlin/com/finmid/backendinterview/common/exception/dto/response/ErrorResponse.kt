package com.finmid.backendinterview.common.exception.dto.response

import com.finmid.backendinterview.common.validation.ValidationError

data class ErrorResponse(
    val errorMsg: String,
    val errors: List<ValidationError> = listOf()
)
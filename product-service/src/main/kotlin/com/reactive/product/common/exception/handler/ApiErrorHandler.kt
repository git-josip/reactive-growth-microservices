package com.reactive.product.common.exception.handler

import com.reactive.product.common.exception.NotFoundException
import com.reactive.product.common.exception.ValidationException
import com.reactive.product.common.exception.dto.response.ErrorResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import reactor.core.publisher.Mono

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class ApiErrorHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(exception: Throwable): Mono<ResponseEntity<ErrorResponse>> =
        Mono.just(
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse(errorMsg = "Resource not found"))
        )

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(exception: ValidationException): Mono<ResponseEntity<ErrorResponse>> =
        Mono.just(
            ResponseEntity
                .badRequest()
                .body(
                    ErrorResponse(
                        errorMsg = "Validation failed",
                        errors = exception.errors
                    )
                )
        )
}

package com.finmid.backendinterview.common.exception.handler

import com.finmid.backendinterview.common.exception.AccountStateChangedException
import com.finmid.backendinterview.common.exception.NotFoundException
import com.finmid.backendinterview.common.exception.ValidationException
import com.finmid.backendinterview.common.exception.dto.response.ErrorResponse
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

    @ExceptionHandler(AccountStateChangedException::class)
    fun handleAccountStateChangedException(exception: AccountStateChangedException): Mono<ResponseEntity<ErrorResponse>> =
        Mono.just(
            ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                    ErrorResponse(
                        errorMsg = "The transaction could not be completed because the account state has changed. Please try again.",
                    )
                )
        )
}

package com.reactive.order.common.exception.handler

import com.google.rpc.Status
import com.reactive.order.common.configuration.ObjectMapperConfiguration
import com.reactive.order.common.exception.NotFoundException
import com.reactive.order.common.exception.ValidationException
import com.reactive.order.common.exception.dto.response.ErrorResponse
import io.grpc.protobuf.StatusProto
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler

@GrpcAdvice
class GrpcExceptionAdvice {
    @GrpcExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(exception: Throwable): io.grpc.Status {
        val status = Status.newBuilder()
            .setCode(io.grpc.Status.NOT_FOUND.code.value())
            .setMessage("Resource not found")
            .build()

        return StatusProto.toStatusException(status).status
    }

    @GrpcExceptionHandler(ValidationException::class)
    fun handleValidationException(exception: ValidationException): io.grpc.Status {
        val errorResponse = ErrorResponse(
            errorMsg = "Validation failed",
            errors = exception.errors
        )

        val status = Status.newBuilder()
            .setCode(io.grpc.Status.INVALID_ARGUMENT.code.value())
            .setMessage(ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(errorResponse))
            .build()

        return StatusProto.toStatusException(status).status
    }
}
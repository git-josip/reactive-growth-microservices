package com.reactive.apigateway.common.exception.handler

import com.reactive.apigateway.common.configuration.ObjectMapperConfiguration
import com.reactive.apigateway.common.exception.ValidationException
import com.reactive.apigateway.common.exception.response.ErrorResponse
import com.linecorp.armeria.common.HttpData
import com.linecorp.armeria.common.HttpRequest
import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.common.HttpStatus
import com.linecorp.armeria.common.MediaType
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.annotation.ExceptionHandlerFunction
import org.slf4j.LoggerFactory

class ValidationExceptionHandler : ExceptionHandlerFunction {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handleException(
        ctx: ServiceRequestContext,
        req: HttpRequest,
        cause: Throwable
    ): HttpResponse {
        if (cause is com.reactive.apigateway.common.exception.ValidationException) {
            return HttpResponse.of(
                HttpStatus.BAD_REQUEST,
                MediaType.JSON,
                HttpData.ofUtf8(
                    ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(
                        ErrorResponse(
                            errorMsg = cause.message,
                            errors = cause.errors
                        )
                    )
                )
            )
        } else if (cause is IllegalArgumentException) {
            return HttpResponse.of(
                HttpStatus.BAD_REQUEST,
                MediaType.JSON,
                HttpData.ofUtf8(
                        ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(
                            ErrorResponse(errorMsg = cause.message ?: "Bad Request")
                        )
                )
            )
        }

        log.error("Unhandled exception occurred", cause)
        return HttpResponse.of(
            HttpStatus.UNPROCESSABLE_ENTITY,
            MediaType.JSON,
            HttpData.ofUtf8(ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(ErrorResponse(errorMsg = "Unprocessable Content. The request was well-formed but could not be processed. Please contact support if this continues.")))
        )
    }
}
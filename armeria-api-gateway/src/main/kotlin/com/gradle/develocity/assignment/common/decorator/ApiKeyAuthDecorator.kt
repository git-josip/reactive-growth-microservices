package com.gradle.develocity.assignment.common.decorator

import com.gradle.develocity.assignment.common.configuration.ObjectMapperConfiguration
import com.gradle.develocity.assignment.common.exception.response.ErrorResponse
import com.gradle.develocity.assignment.common.utils.ApplicationPropertiesUtils
import com.gradle.develocity.assignment.common.utils.HashUtils
import com.gradle.develocity.assignment.common.validation.ValidationError
import com.linecorp.armeria.common.*
import com.linecorp.armeria.server.AnnotatedServiceBindingBuilder
import com.linecorp.armeria.server.DecoratingHttpServiceFunction
import com.linecorp.armeria.server.HttpService
import com.linecorp.armeria.server.ServiceRequestContext

class ApiKeyAuthDecorator : DecoratingHttpServiceFunction {
    override fun serve(delegate: HttpService, ctx: ServiceRequestContext, req: HttpRequest): HttpResponse {
        if (!authenticate(req)) {
            return HttpResponse.of(
                HttpStatus.UNAUTHORIZED,
                MediaType.JSON,
                HttpData.ofUtf8(
                    ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(
                        ErrorResponse(
                            errorMsg = "Access Denied.",
                            errors = listOf(
                                ValidationError(
                                    field = "apikey",
                                    message = "You do not have the necessary permissions to access this resource. Please check your API key or contact support if you believe this is an error."
                                )
                            )
                        )
                    )
                )
            )
        }

        return delegate.serve(ctx, req);
    }

    companion object {
        private const val AUTHORIZATION_HEADER_START = "apikey "
        private val SERVER_API_KEY_SALT = ApplicationPropertiesUtils.getProperty("server.api-key.salt")
        private val SERVER_API_KEY_HASHED = ApplicationPropertiesUtils.getProperty("server.api-key.hashed")

        private fun authenticate(req: HttpRequest): Boolean {
            val authorizationHeader: String? = req.headers().get(HttpHeaderNames.AUTHORIZATION)

            if (authorizationHeader != null && authorizationHeader.startsWith(AUTHORIZATION_HEADER_START)) {
                val apiKey = authorizationHeader.replaceFirst(AUTHORIZATION_HEADER_START, "")

                return SERVER_API_KEY_HASHED == HashUtils.generateHash(apiKey, SERVER_API_KEY_SALT)
            }

            return false
        }

        fun AnnotatedServiceBindingBuilder.applyApiKeyAuthDecorator(): AnnotatedServiceBindingBuilder {
            return this.decorator(ApiKeyAuthDecorator())
        }
    }
}
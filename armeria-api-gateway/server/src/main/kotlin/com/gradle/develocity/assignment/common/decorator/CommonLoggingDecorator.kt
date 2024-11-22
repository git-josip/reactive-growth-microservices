package com.gradle.develocity.assignment.common.decorator

import com.linecorp.armeria.common.logging.LogLevel
import com.linecorp.armeria.common.logging.LogWriter
import com.linecorp.armeria.server.AnnotatedServiceBindingBuilder
import com.linecorp.armeria.server.HttpService
import com.linecorp.armeria.server.logging.LoggingService
import java.util.function.Function

object CommonLoggingDecorator {
    private val infoDecorator: Function<in HttpService, LoggingService> = LoggingService.builder()
            .logWriter(
                LogWriter.builder()
                    .requestLogLevel(LogLevel.INFO)
                    .successfulResponseLogLevel(LogLevel.INFO)
                    .build(),
            )
            .newDecorator()


    fun AnnotatedServiceBindingBuilder.applyCommonLogDecorator(): AnnotatedServiceBindingBuilder {
        return this
            .decorator(CommonLoggingDecorator.infoDecorator)
    }
}
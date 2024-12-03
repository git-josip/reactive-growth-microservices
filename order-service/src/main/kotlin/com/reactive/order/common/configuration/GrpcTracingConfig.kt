package com.reactive.order.common.configuration

import brave.Tracing
import brave.grpc.GrpcTracing
import brave.handler.SpanHandler
import io.grpc.ServerInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import zipkin2.reporter.AsyncReporter
import zipkin2.reporter.Sender
import zipkin2.reporter.okhttp3.OkHttpSender
import zipkin2.reporter.brave.ZipkinSpanHandler

@Configuration
class GrpcTracingConfig {
    @Bean
    fun sender(@Value("\${spring.zipkin.baseUrl}") zipkinBaseUrl: String): Sender {
        // Configure the sender to send spans to Zipkin
        return OkHttpSender.create("$zipkinBaseUrl/api/v2/spans")
    }

    @Bean("zipkinSpanHandler")
    fun zipkinSpanHandler(sender: Sender): SpanHandler {
        // Create an AsyncReporter for better performance
        val reporter = AsyncReporter.create(sender)
        // Create and return the ZipkinSpanHandler
        return ZipkinSpanHandler.create(reporter)
    }

    @Bean
    fun tracing(@Qualifier("zipkinSpanHandler") zipkinSpanHandler: SpanHandler): Tracing {
        // Build and return the Tracing instance
        return Tracing.newBuilder()
            .localServiceName("order-service")
            .addSpanHandler(zipkinSpanHandler)
            .build()
    }

    @Bean
    fun grpcTracing(tracing: Tracing): GrpcTracing {
        return GrpcTracing.create(tracing)
    }

    @Bean
    fun grpcServerInterceptor(grpcTracing: GrpcTracing): ServerInterceptor {
        return grpcTracing.newServerInterceptor()
    }
}

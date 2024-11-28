package com.gradle.develocity.assignment.common.configuration

import com.gradle.develocity.assignment.common.decorator.ApiKeyAuthDecorator
import com.gradle.develocity.assignment.common.decorator.ApiKeyAuthDecorator.Companion.applyApiKeyAuthDecorator
import com.gradle.develocity.assignment.common.utils.ApplicationPropertiesUtils
import com.gradle.develocity.assignment.common.decorator.CommonLoggingDecorator.applyCommonLogDecorator
import com.gradle.develocity.assignment.common.decorator.RateLimitDecorator
import com.gradle.develocity.assignment.common.decorator.RateLimitDecorator.Companion.applyRateLimitDecorator
import com.gradle.develocity.assignment.common.utils.convert
import com.gradle.develocity.assignment.module.product.service.grpc.external.ProductService
import com.linecorp.armeria.common.HttpMethod
import com.linecorp.armeria.common.kotlin.asCoroutineContext
import com.linecorp.armeria.common.metric.MeterIdPrefixFunction
import com.linecorp.armeria.server.*
import com.linecorp.armeria.server.cors.CorsService
import com.linecorp.armeria.server.encoding.DecodingService
import com.linecorp.armeria.server.encoding.EncodingService
import com.linecorp.armeria.server.kotlin.CoroutineContextService
import com.linecorp.armeria.server.metric.MetricCollectingService
import com.linecorp.armeria.server.prometheus.PrometheusExpositionService
import kotlinx.coroutines.CoroutineName
import java.time.Duration

object ServerConfiguration {
    private val API_DOCS_PATH_PREFIX = ApplicationPropertiesUtils.getProperty("server.documentation.path-prefix")
    private val METRICS_PATH_PREFIX = ApplicationPropertiesUtils.getProperty("server.metrics.path-prefix")
    private val PRODUCTS_PATH_PREFIX = ApplicationPropertiesUtils.getProperty("server.products.path-prefix")
    private val PRODUCTS_RATE_LIMIT_DURATION_SECONDS = ApplicationPropertiesUtils.getProperty("products.rate-limit-duration.seconds").convert<Long>()

    fun configureServices(
        serverBuilder: ServerBuilder,
        rateLimit: Long
        ) {
        serverBuilder
            // Prometheus metrics
            .service(
                METRICS_PATH_PREFIX,
                PrometheusExpositionService.of(PrometheusRegistryConfiguration.registry.prometheusRegistry)
                    .decorate(ApiKeyAuthDecorator())
                    .decorate(
                        RateLimitDecorator(
                            requestLimit = 30,
                            limitDuration = Duration.ofSeconds(60)
                        )
                    )
            )
            .meterRegistry(PrometheusRegistryConfiguration.registry)

            // Server Metrics
            .decorator(
                MetricCollectingService.newDecorator(
                    MeterIdPrefixFunction.ofDefault("armeria.http.service")
                )
            )

            // Decode/Encode
            .decorator(DecodingService.newDecorator())
            .decorator(EncodingService.newDecorator())

            // TimeZoneDbExternalService
            .annotatedService()
            .pathPrefix(PRODUCTS_PATH_PREFIX)
            .applyApiKeyAuthDecorator()
            .applyCommonLogDecorator()
            .applyRateLimitDecorator(
                requestLimit = rateLimit,
                limitDuration = Duration.ofSeconds(PRODUCTS_RATE_LIMIT_DURATION_SECONDS)
            )
            .decorator(
                CoroutineContextService.newDecorator {
                    ServiceRequestContext.current().asCoroutineContext() + CoroutineName("apigateway-service-coroutine")
                },
            )
            .decorator(
                CorsService.builderForAnyOrigin()
                        .allowRequestMethods(HttpMethod.GET)
                        .allowRequestHeaders("*")
                        .exposeHeaders("*")
                        .newDecorator()
            )
            .build(ProductService())
    }
}
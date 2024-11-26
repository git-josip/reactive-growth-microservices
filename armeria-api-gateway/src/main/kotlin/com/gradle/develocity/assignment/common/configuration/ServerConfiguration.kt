package com.gradle.develocity.assignment.common.configuration

import com.gradle.develocity.assignment.common.decorator.ApiKeyAuthDecorator
import com.gradle.develocity.assignment.common.decorator.ApiKeyAuthDecorator.Companion.applyApiKeyAuthDecorator
import com.gradle.develocity.assignment.common.utils.ApplicationPropertiesUtils
import com.gradle.develocity.assignment.common.decorator.CommonLoggingDecorator.applyCommonLogDecorator
import com.gradle.develocity.assignment.common.decorator.RateLimitDecorator
import com.gradle.develocity.assignment.common.decorator.RateLimitDecorator.Companion.applyRateLimitDecorator
import com.gradle.develocity.assignment.common.utils.convert
import com.gradle.develocity.assignment.module.timezone.service.http.external.TimeZoneExternalService
import com.linecorp.armeria.common.HttpHeaderNames
import com.linecorp.armeria.common.HttpHeaders
import com.linecorp.armeria.common.HttpMethod
import com.linecorp.armeria.common.kotlin.asCoroutineContext
import com.linecorp.armeria.common.metric.MeterIdPrefixFunction
import com.linecorp.armeria.server.*
import com.linecorp.armeria.server.cors.CorsService
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.encoding.DecodingService
import com.linecorp.armeria.server.encoding.EncodingService
import com.linecorp.armeria.server.kotlin.CoroutineContextService
import com.linecorp.armeria.server.metric.MetricCollectingService
import com.linecorp.armeria.server.prometheus.PrometheusExpositionService
import io.netty.handler.codec.http.HttpHeaderValues
import kotlinx.coroutines.CoroutineName
import java.time.Duration

object ServerConfiguration {
    private val API_DOCS_PATH_PREFIX = ApplicationPropertiesUtils.getProperty("server.documentation.path-prefix")
    private val METRICS_PATH_PREFIX = ApplicationPropertiesUtils.getProperty("server.metrics.path-prefix")
    private val TIMEZONES_PATH_PREFIX = ApplicationPropertiesUtils.getProperty("server.timezones.path-prefix")
    private val TIMEZONES_RATE_LIMIT_DURATION_SECONDS = ApplicationPropertiesUtils.getProperty("timezone.db.rate-limit-duration.seconds").convert<Long>()

    fun configureServices(
        serverBuilder: ServerBuilder,
        rateLimit: Long
        ) {
        serverBuilder

            // API Documentation
            .serviceUnder(API_DOCS_PATH_PREFIX, DocService.builder()
                .exampleQueries(
                    TimeZoneExternalService::class.java,
                    TimeZoneExternalService::getTimezones.name,
                    "city=Denver"
                )
                .exampleHeaders(
                    TimeZoneExternalService::class.java,
                    TimeZoneExternalService::getTimezones.name,
                    listOf(
                        HttpHeaders.of(
                            HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON,
                            HttpHeaderNames.AUTHORIZATION, "apikey %YOUR_API_KEY%"
                        )
                    )
                )
                .build()
                .decorate(
                    RateLimitDecorator(
                        requestLimit = 50,
                        limitDuration = Duration.ofSeconds(60)
                    )
                )
            )

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
            .pathPrefix(TIMEZONES_PATH_PREFIX)
            .applyApiKeyAuthDecorator()
            .applyCommonLogDecorator()
            .applyRateLimitDecorator(
                requestLimit = rateLimit,
                limitDuration = Duration.ofSeconds(TIMEZONES_RATE_LIMIT_DURATION_SECONDS)
            )
            .decorator(
                CoroutineContextService.newDecorator {
                    ServiceRequestContext.current().asCoroutineContext() + CoroutineName("timezones-service-coroutine")
                },
            )
            .decorator(
                CorsService.builderForAnyOrigin()
                        .allowRequestMethods(HttpMethod.GET)
                        .allowRequestHeaders("*")
                        .exposeHeaders("*")
                        .newDecorator()
            )
            .build(TimeZoneExternalService())
    }
}
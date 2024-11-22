package com.gradle.develocity.assignment.common.webclient

import com.gradle.develocity.assignment.common.utils.ApplicationPropertiesUtils
import com.gradle.develocity.assignment.common.utils.convert
import com.linecorp.armeria.client.ClientFactory
import com.linecorp.armeria.client.RestClient
import com.linecorp.armeria.client.WebClient
import com.linecorp.armeria.client.circuitbreaker.CircuitBreaker
import com.linecorp.armeria.client.circuitbreaker.CircuitBreakerClient
import com.linecorp.armeria.client.circuitbreaker.CircuitBreakerRule
import com.linecorp.armeria.client.encoding.DecodingClient
import com.linecorp.armeria.client.limit.ConcurrencyLimitingClient
import com.linecorp.armeria.client.logging.LoggingClient
import com.linecorp.armeria.client.metric.MetricCollectingClient
import com.linecorp.armeria.client.retry.RetryRule
import com.linecorp.armeria.client.retry.RetryingClient
import com.linecorp.armeria.common.HttpStatusClass
import com.linecorp.armeria.common.metric.MeterIdPrefixFunction

object RestClientFactory {
    private val REQUEST_DEBUG = ApplicationPropertiesUtils.getProperty("request.debug").convert<Boolean>()

    val clientFactory = ClientFactory.builder()
        .maxNumEventLoopsPerEndpoint(16)
        .maxNumEventLoopsPerHttp1Endpoint(16)
        .build()

    private val clientCircuitBreakerRule: CircuitBreakerRule = CircuitBreakerRule.of(
        CircuitBreakerRule.onException(),
        CircuitBreakerRule.builder()
            .onUnprocessed()
            .thenIgnore(),
        CircuitBreakerRule.onServerErrorStatus(),
        CircuitBreakerRule.builder()
            .onStatusClass(HttpStatusClass.CLIENT_ERROR)
            .thenSuccess(),
        CircuitBreakerRule.builder()
            .onStatusClass(HttpStatusClass.SUCCESS)
            .thenSuccess(),
        CircuitBreakerRule.builder().thenIgnore()
    )

    fun of(name: String, concurrencyLimit: Int): RestClient {
        val webClientBuilder = WebClient
            .builder()
            .factory(clientFactory)
            .decorator(
                CircuitBreakerClient.newDecorator(
                    CircuitBreaker.of("$name-circuit-breaker"),
                    clientCircuitBreakerRule
                )
            )
            .decorator(ConcurrencyLimitingClient.newDecorator(concurrencyLimit))
            .decorator(DecodingClient.newDecorator())
            .decorator(
                MetricCollectingClient.newDecorator(
                    MeterIdPrefixFunction.ofDefault(name)
                )
            )
            .decorator(RetryingClient.newDecorator(RetryRule.onServerErrorStatus()))

        if(REQUEST_DEBUG) {
            webClientBuilder.decorator(LoggingClient.newDecorator())
        }

        return webClientBuilder
            .build()
            .asRestClient()
    }
}
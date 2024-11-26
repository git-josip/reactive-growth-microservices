package com.gradle.develocity.assignment.common.configuration

import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

object PrometheusRegistryConfiguration {
    val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
}
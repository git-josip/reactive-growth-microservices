package com.gradle.develocity.assignment

import com.gradle.develocity.assignment.common.configuration.ServerConfiguration
import com.gradle.develocity.assignment.common.utils.ApplicationPropertiesUtils
import com.gradle.develocity.assignment.common.utils.convert
import com.linecorp.armeria.server.Server
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("Main")

private const val KOTLINX_COROUTINES_DEBUG_PROPERTY_NAME = "kotlinx.coroutines.debug"
private val SERVER_HTTP_PORT = ApplicationPropertiesUtils.getProperty("server.http.port").convert<Int>()
private val SERVER_HTTPS_PORT = ApplicationPropertiesUtils.getProperty("server.https.port").convert<Int>()
private val KOTLINX_COROUTINES_DEBUG = ApplicationPropertiesUtils.getProperty(KOTLINX_COROUTINES_DEBUG_PROPERTY_NAME)
private val TIMEZONES_RATE_LIMIT = ApplicationPropertiesUtils.getProperty("timezone.db.rate-limit").convert<Long>()

suspend fun main() {
    System.setProperty(KOTLINX_COROUTINES_DEBUG_PROPERTY_NAME, KOTLINX_COROUTINES_DEBUG)
    val serverBuilder = Server.builder()
    .http(SERVER_HTTP_PORT)
    .https(SERVER_HTTPS_PORT)
    .tlsSelfSigned()

    ServerConfiguration.configureServices(serverBuilder, TIMEZONES_RATE_LIMIT)
    val server =  serverBuilder.build()
    server.closeOnJvmShutdown()
    server.start().join()

    log.info("HTTP => API documentation at: http://127.0.0.1:$SERVER_HTTP_PORT/documentation")
    log.info("HTTP => API documentation at: https://127.0.0.1:$SERVER_HTTPS_PORT/documentation")
}

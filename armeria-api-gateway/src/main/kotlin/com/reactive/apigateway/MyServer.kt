package com.reactive.apigateway

import com.reactive.apigateway.common.configuration.ServerConfiguration
import com.reactive.apigateway.common.utils.ApplicationPropertiesUtils
import com.reactive.apigateway.common.utils.convert
import com.linecorp.armeria.server.Server
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("Main")

private const val KOTLINX_COROUTINES_DEBUG_PROPERTY_NAME = "kotlinx.coroutines.debug"
private val SERVER_HTTP_PORT = ApplicationPropertiesUtils.getProperty("server.http.port").convert<Int>()
private val KOTLINX_COROUTINES_DEBUG = ApplicationPropertiesUtils.getProperty(KOTLINX_COROUTINES_DEBUG_PROPERTY_NAME)
private val PRODUCTS_RATE_LIMIT = ApplicationPropertiesUtils.getProperty("products.rate-limit").convert<Long>()

suspend fun main() {
    System.setProperty(KOTLINX_COROUTINES_DEBUG_PROPERTY_NAME, KOTLINX_COROUTINES_DEBUG)
    val serverBuilder = Server.builder()
    .http(SERVER_HTTP_PORT)

    ServerConfiguration.configureServices(serverBuilder, PRODUCTS_RATE_LIMIT)
    val server =  serverBuilder.build()
    server.closeOnJvmShutdown()
    server.start().join()

    log.info("HTTP => API documentation at: http://127.0.0.1:$SERVER_HTTP_PORT/documentation")
}

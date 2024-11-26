package com.reactive.recommendation.utils

import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.PostgreSQLContainer

object PropertyUtils {
    fun configureCommonDynamicProperties(registry: DynamicPropertyRegistry, postgres: PostgreSQLContainer<*>) {
        registry.add("spring.r2dbc.url") { "r2dbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/${postgres.databaseName}" }
        registry.add("spring.r2dbc.username", postgres::getUsername)
        registry.add("spring.r2dbc.password", postgres::getPassword)
        registry.add("spring.flyway.url", postgres::getJdbcUrl)
        registry.add("spring.flyway.user", postgres::getUsername)
        registry.add("spring.flyway.password", postgres::getPassword)
    }
}
package com.reactive.productcomposite

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import reactor.core.publisher.Hooks

@SpringBootApplication
@EnableR2dbcRepositories
@OpenAPIDefinition
@ConfigurationPropertiesScan
class FinmidBackendInterviewApplication

fun main(args: Array<String>) {
	runApplication<FinmidBackendInterviewApplication>(*args)
	Hooks.enableAutomaticContextPropagation()
}


package com.reactive.order.common.configuration.properties

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "basic-auth")
data class BasicAuthProperties(
    @NotBlank
    var username: String = "",

    @NotBlank
    var password: String = ""
)
package com.reactive.product.common.configuration

import org.apache.kafka.clients.admin.AdminClient
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaAdminConfig(private val kafkaProperties: KafkaProperties) {
    @Bean
    fun adminClient(): AdminClient {
        return AdminClient.create(kafkaProperties.buildAdminProperties(null))
    }
}
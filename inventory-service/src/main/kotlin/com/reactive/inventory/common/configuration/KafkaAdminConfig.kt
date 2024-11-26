package com.reactive.inventory.common.configuration

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaAdminConfig {
    @Bean
    fun adminClient(): AdminClient {
        val configs = mapOf(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:29092"
        )
        return AdminClient.create(configs)
    }
}
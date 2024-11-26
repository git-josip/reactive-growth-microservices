package com.reactive.product.module.transaction.event.kafka

import kotlinx.coroutines.future.asDeferred
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(private val kafkaTemplate: KafkaTemplate<String, String>) {
    suspend fun sendMessages(topic: String, key: String, message: String): SendResult<String, String> {
        return kafkaTemplate.send(topic, key, message)
            .asDeferred()
            .await()
    }
}
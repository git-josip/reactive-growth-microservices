package com.reactive.inventory.module.inventory.event.kafka

import com.reactive.inventory.common.configuration.ObjectMapperConfiguration
import com.reactive.inventory.module.inventory.event.domain.ProductCreatedEvent
import com.reactive.inventory.module.inventory.mapper.toInventoryCreate
import com.reactive.inventory.module.inventory.service.IInventoryService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaProductCreatedConsumerService(
    private val transactionService: IInventoryService
) {
    @KafkaListener(topics = ["products"], groupId = "inventory-service", concurrency = "5")
    suspend fun listen(record: ConsumerRecord<String, String>) {
        val productCreated = ObjectMapperConfiguration.jacksonObjectMapper.readValue(record.value(), ProductCreatedEvent::class.java)

        transactionService.create(productCreated.toInventoryCreate())
    }

}
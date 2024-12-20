package com.reactive.inventory.module.inventory.event.kafka

import com.reactive.inventory.common.configuration.ObjectMapperConfiguration
import com.reactive.inventory.module.inventory.event.domain.ProductCreatedEvent
import com.reactive.inventory.module.inventory.mapper.toInventoryCreate
import com.reactive.inventory.module.inventory.service.IInventoryService
import io.micrometer.tracing.Tracer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaProductCreatedConsumerService(
    private val inventoryService: IInventoryService,
    private val tracer: Tracer
) {
    @KafkaListener(topics = ["products"], groupId = "inventory-service", concurrency = "5")
    suspend fun listen(record: ConsumerRecord<String, String>) {
        val productCreated = ObjectMapperConfiguration.jacksonObjectMapper.readValue(record.value(), ProductCreatedEvent::class.java)

        val newSpan = tracer.nextSpan().name("inventoryService.create").start()
        try {
            tracer.withSpan(newSpan).use {
                inventoryService.create(productCreated.toInventoryCreate())
            }
        } finally {
            newSpan.end()
        }
    }

}
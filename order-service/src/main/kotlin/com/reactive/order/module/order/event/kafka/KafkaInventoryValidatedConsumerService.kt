package com.reactive.order.module.order.event.kafka

import com.reactive.order.common.configuration.ObjectMapperConfiguration
import com.reactive.order.module.order.event.domain.InventoryValidatedEvent
import com.reactive.order.module.order.service.IOrderService
import io.micrometer.tracing.Tracer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaInventoryValidatedConsumerService(
    private val orderService: IOrderService,
    private val tracer: Tracer
) {
    @KafkaListener(topics = ["inventory_validation"], groupId = "order-service")
    suspend fun listen(record: ConsumerRecord<String, String>) {
        val inventoryValidated = ObjectMapperConfiguration.jacksonObjectMapper.readValue(record.value(), InventoryValidatedEvent::class.java)

        if(inventoryValidated.context == "ORDER") {
            orderService.tryGetById(inventoryValidated.contextRefId)?.let { order ->
                val newSpan = tracer.nextSpan().name("orderService.orderUpdated").start()
                try {
                    tracer.withSpan(newSpan).use {
                        val updatedOrder = orderService.update(
                            order.copy(
                                status = inventoryValidated.status,
                                details = inventoryValidated.details
                            )
                        )

                        orderService.orderUpdated(updatedOrder)
                    }
                } finally {
                    newSpan.end()
                }
            }
        }
    }

}
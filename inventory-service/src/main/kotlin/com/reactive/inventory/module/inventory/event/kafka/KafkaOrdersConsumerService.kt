package com.reactive.inventory.module.inventory.event.kafka

import com.reactive.inventory.common.configuration.ObjectMapperConfiguration
import com.reactive.inventory.module.inventory.event.domain.InventoryValidatedEvent
import com.reactive.inventory.module.inventory.event.domain.OrderEvent
import com.reactive.inventory.module.inventory.service.IInventoryService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaOrdersConsumerService(
    private val inventoryService: IInventoryService,
    private val kafkaProducerService: KafkaProducerService
) {
    @KafkaListener(topics = ["orders"], groupId = "inventory-service", concurrency = "5")
    suspend fun listen(record: ConsumerRecord<String, String>) {
        val order = ObjectMapperConfiguration.jacksonObjectMapper.readValue(record.value(), OrderEvent::class.java)

        if(order.status == "CREATED") {
            inventoryService.tryGetByProductId(order.productId)?.let { inventory ->
                if(inventory.quantity >= order.quantity) {
                    val newInventory = inventory.copy(quantity = inventory.quantity - order.quantity)
                    inventoryService.update(newInventory)

                    val inventoryValidatedEvent = InventoryValidatedEvent(
                        context = "ORDER",
                        contextRefId = order.orderId,
                        productId = order.productId,
                        quantity = newInventory.quantity,
                        status = "SUCCESS"
                    )
                    kafkaProducerService.sendMessages(
                        "inventory_validation",
                        "${inventoryValidatedEvent.context}-${inventoryValidatedEvent.contextRefId}",
                        ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(inventoryValidatedEvent),
                    )
                } else {
                    val inventoryValidatedEvent = InventoryValidatedEvent(
                        context = "ORDER",
                        contextRefId = order.orderId,
                        productId = order.productId,
                        quantity = inventory.quantity,
                        status = "FAILED",
                        details = "Insufficient inventory"
                    )
                    kafkaProducerService.sendMessages(
                        "inventory_validation",
                        "${inventoryValidatedEvent.context}-${inventoryValidatedEvent.contextRefId}",
                        ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(inventoryValidatedEvent),
                    )
                }
            }
        }
    }

}
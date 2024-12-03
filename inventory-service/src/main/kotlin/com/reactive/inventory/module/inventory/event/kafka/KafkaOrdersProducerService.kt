package com.reactive.inventory.module.inventory.event.kafka

import com.reactive.inventory.common.configuration.ObjectMapperConfiguration
import com.reactive.inventory.module.inventory.domain.Inventory
import com.reactive.inventory.module.inventory.event.domain.InventoryValidatedEvent
import com.reactive.inventory.module.inventory.event.domain.OrderEvent
import org.springframework.cloud.sleuth.annotation.ContinueSpan
import org.springframework.stereotype.Service

@Service
class KafkaOrdersProducerService(private val kafkaProducerService: KafkaProducerService) {
    @ContinueSpan(log = "InventoryService.orderValidationSuccess")
    suspend fun orderValidationSuccess(orderEvent: OrderEvent, newInventory: Inventory) {
        val inventoryValidatedEvent = InventoryValidatedEvent(
            context = "ORDER",
            contextRefId = orderEvent.orderId,
            productId = orderEvent.productId,
            quantity = newInventory.quantity,
            status = "SUCCESS"
        )
        kafkaProducerService.sendMessages(
            "inventory_validation",
            "${inventoryValidatedEvent.context}-${inventoryValidatedEvent.contextRefId}",
            ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(inventoryValidatedEvent),
        )
    }

    @ContinueSpan(log = "InventoryService.orderValidationFailed")
    suspend fun orderValidationFailed(orderEvent: OrderEvent, inventory: Inventory) {
        val inventoryValidatedEvent = InventoryValidatedEvent(
            context = "ORDER",
            contextRefId = orderEvent.orderId,
            productId = orderEvent.productId,
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
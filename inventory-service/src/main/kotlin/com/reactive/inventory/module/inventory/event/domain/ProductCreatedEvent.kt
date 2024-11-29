package com.reactive.inventory.module.inventory.event.domain

data class ProductCreatedEvent(
    val productId: Long,
    val quantity: Int
)
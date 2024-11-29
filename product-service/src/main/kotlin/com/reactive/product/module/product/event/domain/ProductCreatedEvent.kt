package com.reactive.product.module.product.event.domain

data class ProductCreatedEvent(
    val productId: Long,
    val quantity: Int
)
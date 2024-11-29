package com.reactive.product.module.product.event.domain

data class InventoryValidatedEvent(
    val context: String,
    val contextRefId: Long,
    val productId: Long,
    val quantity: Int,
    val status: String,
    val details: String?
)
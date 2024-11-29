package com.reactive.inventory.module.inventory.domain

data class InventoryCreate(
    val productId: Long,
    val quantity: Int
)

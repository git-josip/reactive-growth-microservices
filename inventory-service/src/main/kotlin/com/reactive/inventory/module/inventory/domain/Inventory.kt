package com.reactive.inventory.module.inventory.domain

import java.time.LocalDateTime

data class Inventory(
    val productId: Long,
    val quantity: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

package com.reactive.order.module.order.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class Order(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal,
    val status: String,
    val details: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

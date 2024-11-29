package com.reactive.order.module.order.event.domain

import java.math.BigDecimal

data class OrderEvent(
    val orderId: Long,
    val type: String,
    val status: String,
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal,
    val details: String? = null
)
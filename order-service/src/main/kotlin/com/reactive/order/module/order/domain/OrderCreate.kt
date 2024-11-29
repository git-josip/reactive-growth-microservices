package com.reactive.order.module.order.domain

import java.math.BigDecimal

data class OrderCreate(
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal
)
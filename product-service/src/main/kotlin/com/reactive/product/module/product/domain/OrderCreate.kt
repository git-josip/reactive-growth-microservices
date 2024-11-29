package com.reactive.product.module.product.domain

import java.math.BigDecimal

data class OrderCreate(
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal
)

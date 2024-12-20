package com.reactive.product.module.product.domain

import java.math.BigDecimal

data class ProductCreate(
    val name: String,
    val category: String,
    val quantity: Int,
    val price: BigDecimal
)

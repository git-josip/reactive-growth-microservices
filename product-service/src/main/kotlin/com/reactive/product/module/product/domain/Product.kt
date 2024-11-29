package com.reactive.product.module.product.domain

import java.math.BigDecimal

data class Product(
    val id: Long,
    val name: String,
    val category: String,
    val price: BigDecimal
)

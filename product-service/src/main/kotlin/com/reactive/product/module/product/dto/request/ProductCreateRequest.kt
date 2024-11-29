package com.reactive.product.module.product.dto.request

import java.math.BigDecimal

data class ProductCreateRequest(
    val name: String,
    val category: String,
    val quantity: Int,
    val price: BigDecimal
)

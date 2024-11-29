package com.reactive.product.module.product.dto.response

import java.math.BigDecimal

data class ProductResponse(
    val id: Long,
    val name: String,
    val category: String,
    val price: BigDecimal
)

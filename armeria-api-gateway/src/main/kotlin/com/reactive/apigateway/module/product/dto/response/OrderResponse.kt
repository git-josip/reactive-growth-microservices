package com.reactive.apigateway.module.product.dto.response

import java.math.BigDecimal

data class OrderResponse(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal,
    val status: String,
    val details: String?,
    val createdAt: String,
    val updatedAt: String
)
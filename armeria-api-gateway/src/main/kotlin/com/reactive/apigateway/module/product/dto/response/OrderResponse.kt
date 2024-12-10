package com.reactive.apigateway.module.product.dto.response

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal,
    val status: String,
    val details: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
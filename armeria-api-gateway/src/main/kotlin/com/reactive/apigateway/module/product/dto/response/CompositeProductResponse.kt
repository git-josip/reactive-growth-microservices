package com.reactive.apigateway.module.product.dto.response

import java.math.BigDecimal

data class CompositeProductResponse(
    val id: Long,
    val name: String,
    val category: String,
    val price: BigDecimal,
    val inventory: InventoryResponse,
    val orders: List<OrderResponse>
)
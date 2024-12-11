package com.reactive.apigateway.module.product.dto.response

data class InventoryResponse(
    val quantity: Int,
    val createdAt: String,
    val updatedAt: String
)
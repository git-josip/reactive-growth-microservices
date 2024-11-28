package com.reactive.apigateway.module.product.dto.response

data class ProductResponse(
    val id: Long,
    val name: String,
    val category: String,
)
package com.reactive.apigateway.module.product.dto.response

data class CompositeProductResponse(
    val product: ProductResponse,
    val orders: List<OrderResponse>
)
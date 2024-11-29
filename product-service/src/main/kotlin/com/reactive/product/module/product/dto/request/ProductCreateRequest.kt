package com.reactive.product.module.product.dto.request

data class ProductCreateRequest(
    val name: String,
    val category: String,
    val quantity: Int
)

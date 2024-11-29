package com.reactive.product.module.product.domain

data class ProductCreate(
    val name: String,
    val category: String,
    val quantity: Int
)

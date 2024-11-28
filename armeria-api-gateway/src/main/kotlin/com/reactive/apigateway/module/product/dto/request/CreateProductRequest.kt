package com.reactive.apigateway.module.product.dto.request

import com.linecorp.armeria.server.annotation.Description

data class CreateProductRequest(
    @Description("product name")
    val name: String,

    @Description("product category")
    val category: String,
)
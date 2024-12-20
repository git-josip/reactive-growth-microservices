package com.reactive.apigateway.module.product.dto.request

import com.linecorp.armeria.server.annotation.Description
import java.math.BigDecimal

data class CreateProductRequest(
    @Description("product name")
    val name: String,

    @Description("product category")
    val category: String,

    @Description("product quantity")
    val quantity: Int,

    @Description("product price")
    val price: BigDecimal
)
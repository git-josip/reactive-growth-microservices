package com.reactive.apigateway.module.product.dto.request

import com.linecorp.armeria.server.annotation.Description
import java.math.BigDecimal

data class CreateOrderRequest(
    @Description("product quantity")
    val quantity: Int,

    @Description("product price")
    val price: BigDecimal
)
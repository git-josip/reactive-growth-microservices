package com.reactive.apigateway.module.product.mapper

import com.reactive.apigateway.module.product.dto.request.CreateOrderRequest
import com.reactive.apigateway.module.product.dto.request.CreateProductRequest
import java.math.BigDecimal

fun com.reactive.apigateway.grpc.product.ProductResponse.toDomainProductResponse(): com.reactive.apigateway.module.product.dto.response.ProductResponse {
    return com.reactive.apigateway.module.product.dto.response.ProductResponse(
        id = this.id,
        name = this.name,
        category = this.category,
        price = BigDecimal(this.price)
    )
}

fun CreateProductRequest.toGrpcCreateProductRequest(): com.reactive.apigateway.grpc.product.CreateProductRequest {
    return com.reactive.apigateway.grpc.product.CreateProductRequest.newBuilder()
        .setName(this.name)
        .setCategory(this.category)
        .setQuantity(this.quantity)
        .setPrice(this.price.toString())
        .build()
}

fun CreateOrderRequest.toGrpcCreateOrderRequest(productId: Long): com.reactive.apigateway.grpc.product.CreateOrderRequest {
    return com.reactive.apigateway.grpc.product.CreateOrderRequest.newBuilder()
        .setProductId(productId)
        .setQuantity(this.quantity)
        .setPrice(this.price.toString())
        .build()
}
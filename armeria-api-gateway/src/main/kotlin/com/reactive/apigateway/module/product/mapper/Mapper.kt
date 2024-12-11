package com.reactive.apigateway.module.product.mapper

import com.reactive.apigateway.module.product.dto.request.CreateOrderRequest
import com.reactive.apigateway.module.product.dto.request.CreateProductRequest
import com.reactive.apigateway.module.product.dto.response.OrderResponse
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneOffset

private val DATE_FORMATTER = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

fun com.reactive.apigateway.grpc.product.ProductResponse.toDomainProductResponse(): com.reactive.apigateway.module.product.dto.response.ProductResponse {
    return com.reactive.apigateway.module.product.dto.response.ProductResponse(
        id = this.id,
        name = this.name,
        category = this.category,
        price = BigDecimal(this.price)
    )
}

fun com.reactive.apigateway.grpc.inventory.InventoryResponse.toDomainInventoryResponse(): com.reactive.apigateway.module.product.dto.response.InventoryResponse {
    return com.reactive.apigateway.module.product.dto.response.InventoryResponse(
        quantity = this.quantity,
        createdAt = DATE_FORMATTER.format(Instant.ofEpochSecond(this.createdAt.seconds, this.createdAt.nanos.toLong())),
        updatedAt =  DATE_FORMATTER.format(Instant.ofEpochSecond(this.updatedAt.seconds, this.updatedAt.nanos.toLong())),
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

fun com.reactive.apigateway.grpc.order.OrderResponse.toDomainOrderResponse(): OrderResponse {
    return OrderResponse(
        id = this.id,
        productId = this.productId,
        quantity = this.quantity,
        price = BigDecimal(this.price),
        status = this.status,
        details = this.details,
        createdAt = DATE_FORMATTER.format(Instant.ofEpochSecond(this.createdAt.seconds, this.createdAt.nanos.toLong())),
        updatedAt =  DATE_FORMATTER.format(Instant.ofEpochSecond(this.updatedAt.seconds, this.updatedAt.nanos.toLong())),
    )
}
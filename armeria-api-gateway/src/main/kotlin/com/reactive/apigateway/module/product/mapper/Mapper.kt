package com.reactive.apigateway.module.product.mapper

import com.reactive.apigateway.module.product.dto.request.CreateProductRequest

fun com.reactive.apigateway.grpc.product.ProductResponse.toDomainProductResponse(): com.reactive.apigateway.module.product.dto.response.ProductResponse {
    return com.reactive.apigateway.module.product.dto.response.ProductResponse(
        id = this.id,
        name = this.name,
        category = this.category
    )
}

fun CreateProductRequest.toGrpcCreateProductRequest(): com.reactive.apigateway.grpc.product.CreateProductRequest {
    return com.reactive.apigateway.grpc.product.CreateProductRequest.newBuilder()
        .setName(this.name)
        .setCategory(this.category)
        .build()
}
package com.gradle.develocity.assignment.module.product.mapper

import com.gradle.develocity.assignment.module.product.dto.request.CreateProductRequest
import com.gradle.develocity.assignment.module.product.dto.response.ProductResponse

fun com.gradle.develocity.product.module.product.grpc.dto.ProductResponse.toDomainProductResponse(): ProductResponse {
    return ProductResponse(
        id = this.id,
        name = this.name,
        category = this.category
    )
}

fun CreateProductRequest.toGrpcCreateProductRequest(): com.gradle.develocity.product.module.product.grpc.dto.CreateProductRequest {
    return com.gradle.develocity.product.module.product.grpc.dto.CreateProductRequest.newBuilder()
        .setName(this.name)
        .setCategory(this.category)
        .build()
}
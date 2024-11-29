package com.reactive.product.module.product.mapper

import com.reactive.product.common.exception.dto.response.ErrorResponse
import com.reactive.product.common.validation.ValidationError
import com.reactive.product.database.jooq.tables.records.ProductsRecord
import com.reactive.product.module.product.domain.Product
import com.reactive.product.module.product.domain.ProductCreate
import com.reactive.product.module.product.dto.request.ProductCreateRequest
import com.reactive.product.module.product.dto.response.ProductResponse
import com.reactive.product.module.product.event.domain.ProductCreatedEvent
import com.reactive.product.module.product.grpc.dto.CreateProductRequest

fun ProductsRecord.toProduct(): Product {
    return Product(
        id = this.id!!,
        name = this.name,
        category = this.category
    )
}

fun Product.toProductResponse(): ProductResponse {
    return ProductResponse(
        id = this.id,
        name = this.name,
        category = this.category
    )
}

fun Product.toGrpcProductResponse(): com.reactive.product.module.product.grpc.dto.ProductResponse {
    return com.reactive.product.module.product.grpc.dto.ProductResponse.newBuilder()
        .setId(this.id)
        .setName(this.name)
        .setCategory(this.category)
        .build()

}

fun Product.toProductCreatedEvent(quantity: Int): ProductCreatedEvent {
    return ProductCreatedEvent(
        productId = this.id,
         quantity = quantity
    )

}

fun ProductCreateRequest.toProductCreate(): ProductCreate {
    return ProductCreate(
        name = this.name,
        category = this.category,
        quantity = this.quantity
    )
}

fun CreateProductRequest.toProductCreate(): ProductCreate {
    return ProductCreate(
        name = this.name,
        category = this.category,
        quantity = this.quantity
    )
}

fun ProductCreate.toProductsRecord(): ProductsRecord {
    return ProductsRecord(
        id = null,
        name = this.name,
        category = this.category
    )
}

fun ErrorResponse.toGrpcErrorResponse(): com.reactive.product.module.product.grpc.dto.ErrorResponse {
    return com.reactive.product.module.product.grpc.dto.ErrorResponse.newBuilder()
        .setErrorMsg(this.errorMsg)
        .addAllErrors(this.errors.map { it.toGrpcValidationError() })
        .build()
}

fun ValidationError.toGrpcValidationError(): com.reactive.product.module.product.grpc.dto.ValidationError {
    return com.reactive.product.module.product.grpc.dto.ValidationError.newBuilder()
        .setField(this.field)
        .setMessage(this.message)
        .build()
}

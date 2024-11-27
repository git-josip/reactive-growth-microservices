package com.reactive.product.module.product.mapper

import com.reactive.product.database.jooq.tables.records.ProductsRecord
import com.reactive.product.module.product.domain.Product
import com.reactive.product.module.product.domain.ProductCreate
import com.reactive.product.module.product.dto.request.ProductCreateRequest
import com.reactive.product.module.product.dto.response.ProductResponse

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

fun ProductCreateRequest.toProductCreate(): ProductCreate {
    return ProductCreate(
        name = this.name,
        category = this.category
    )
}

fun ProductCreate.toProductsRecord(): ProductsRecord {
    return ProductsRecord(
        id = null,
        name = this.name,
        category = this.category
    )
}

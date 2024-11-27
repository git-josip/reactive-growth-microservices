package com.reactive.product.module.product.handler.grpc.v1

import com.reactive.product.module.product.grpc.dto.*
import com.reactive.product.module.product.mapper.toGrpcProductResponse
import com.reactive.product.module.product.mapper.toProductCreate
import com.reactive.product.module.product.service.IProductService
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
class ProductGrpcServiceImpl(
    val productService: IProductService
): ProductServiceGrpcKt.ProductServiceCoroutineImplBase() {
    override suspend fun createProduct(request: CreateProductRequest): ProductResponse {
        return productService.create(request.toProductCreate())
            .toGrpcProductResponse()
    }

    override suspend fun getProductById(request: GetByIdRequest): ProductResponse {
        return productService.getById(request.id)
            .toGrpcProductResponse()
    }
}
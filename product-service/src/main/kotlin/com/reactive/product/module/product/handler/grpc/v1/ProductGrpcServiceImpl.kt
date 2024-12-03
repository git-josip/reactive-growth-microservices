package com.reactive.product.module.product.handler.grpc.v1

import com.reactive.product.module.product.grpc.dto.*
import com.reactive.product.module.product.mapper.toGrpcOrderCreate
import com.reactive.product.module.product.mapper.toGrpcProductResponse
import com.reactive.product.module.product.mapper.toProductCreate
import com.reactive.product.module.product.service.IProductService
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.cloud.sleuth.annotation.NewSpan

@GrpcService
class ProductGrpcServiceImpl(
    val productService: IProductService
): ProductServiceGrpcKt.ProductServiceCoroutineImplBase() {
    @NewSpan("create-product-by-grpc")
    override suspend fun createProduct(request: CreateProductRequest): ProductResponse {
        return productService.create(request.toProductCreate())
            .toGrpcProductResponse()
    }

    @NewSpan("get-product-by-id-grpc")
    override suspend fun getProductById(request: GetByIdRequest): ProductResponse {
        return productService.getById(request.id)
            .toGrpcProductResponse()
    }

    @NewSpan("create-order-grpc")
    override suspend fun createOrder(request: CreateOrderRequest): EmptyResponse {
        productService.createOrder(request.toGrpcOrderCreate())
        return EmptyResponse.getDefaultInstance()
    }
}
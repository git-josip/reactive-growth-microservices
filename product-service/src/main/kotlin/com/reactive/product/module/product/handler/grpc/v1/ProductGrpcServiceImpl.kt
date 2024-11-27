package com.reactive.product.module.product.handler.grpc.v1

import com.reactive.product.module.product.grpc.dto.CreateProductRequest
import com.reactive.product.module.product.grpc.dto.ProductResponse
import com.reactive.product.module.product.grpc.dto.ProductServiceGrpc
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
class ProductGrpcServiceImpl: ProductServiceGrpc.ProductServiceImplBase() {
    override fun createProduct(request: CreateProductRequest, responseObserver: StreamObserver<ProductResponse>?) {
        responseObserver?.onNext(
            ProductResponse.newBuilder()
                .setId(10)
                .setName(request.name)
                .setCategory(request.category)
                .build()
        )
        responseObserver?.onCompleted()
    }
}
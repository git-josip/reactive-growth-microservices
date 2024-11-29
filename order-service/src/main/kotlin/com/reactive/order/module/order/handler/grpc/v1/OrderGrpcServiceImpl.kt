package com.reactive.order.module.order.handler.grpc.v1

import com.reactive.order.module.order.grpc.dto.*
import com.reactive.order.module.order.mapper.toDomainOrderCreate
import com.reactive.order.module.order.mapper.toGrpcOrderResponse
import com.reactive.order.module.order.service.IOrderService
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
class OrderGrpcServiceImpl(
    private val orderService: IOrderService
): OrderServiceGrpcKt.OrderServiceCoroutineImplBase() {
    override suspend fun createOrder(request: CreateOrderRequest): OrderResponse {
        return orderService.create(request.toDomainOrderCreate())
            .toGrpcOrderResponse()
    }

    override suspend fun getOrderById(request: GetByIdRequest): OrderResponse {
        return orderService.getById(request.id)
            .toGrpcOrderResponse()
    }
}
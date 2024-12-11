package com.reactive.inventory.module.inventory.handler.grpc.v1

import com.reactive.inventory.module.inventory.grpc.dto.*
import com.reactive.inventory.module.inventory.mapper.toGrpcInventoryResponse
import com.reactive.inventory.module.inventory.service.IInventoryService
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
class OrderGrpcServiceImpl(
    private val inventoryService: IInventoryService
): InventoryServiceGrpcKt.InventoryServiceCoroutineImplBase() {
    override suspend fun getInventoryByProductId(request: GetByProductIdRequest): InventoryResponse {
        return inventoryService.getByProductId(request.productId)
            .toGrpcInventoryResponse()
    }
}
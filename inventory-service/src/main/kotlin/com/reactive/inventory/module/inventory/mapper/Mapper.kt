package com.reactive.inventory.module.inventory.mapper

import com.reactive.inventory.database.jooq.tables.records.InventoryRecord
import com.reactive.inventory.module.inventory.domain.Inventory
import com.reactive.inventory.module.inventory.domain.InventoryCreate
import com.reactive.inventory.module.inventory.event.domain.ProductCreatedEvent
import java.time.ZoneOffset

fun  InventoryRecord.toInventory(): Inventory {
    return Inventory(
        productId = this.productId,
        quantity = this.quantity,
        createdAt = this.createdAt!!,
        updatedAt = this.updatedAt!!
    )
}

fun Inventory.toInventoryRecord(): InventoryRecord {
    return InventoryRecord(
        productId = this.productId,
        quantity = this.quantity,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun InventoryCreate.toInventoryRecord(): InventoryRecord {
    return InventoryRecord(
        productId = this.productId,
        quantity = this.quantity,
    )
}

fun ProductCreatedEvent.toInventoryCreate(): InventoryCreate {
    return InventoryCreate(
        productId = this.productId,
        quantity = this.quantity,
    )
}

fun Inventory.toGrpcInventoryResponse(): com.reactive.inventory.module.inventory.grpc.dto.InventoryResponse {
    return com.reactive.inventory.module.inventory.grpc.dto.InventoryResponse.newBuilder()
        .setProductId(this.productId)
        .setQuantity(this.quantity)
        .setCreatedAt(
            com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(this.createdAt.toEpochSecond(java.time.ZoneOffset.UTC))
                .setNanos(this.createdAt.nano)
                .build()
        )
        .setUpdatedAt(
            com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(this.updatedAt.toEpochSecond(java.time.ZoneOffset.UTC))
                .setNanos(this.updatedAt.nano)
                .build()
        )
        .build()
}
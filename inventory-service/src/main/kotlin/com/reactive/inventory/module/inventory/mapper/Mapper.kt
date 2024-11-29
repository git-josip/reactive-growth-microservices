package com.reactive.inventory.module.inventory.mapper

import com.reactive.inventory.database.jooq.tables.records.InventoryRecord
import com.reactive.inventory.module.inventory.domain.Inventory
import com.reactive.inventory.module.inventory.domain.InventoryCreate
import com.reactive.inventory.module.inventory.event.domain.ProductCreatedEvent

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
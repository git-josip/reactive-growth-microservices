package com.reactive.inventory.module.inventory.service

import com.reactive.inventory.common.exception.NotFoundException
import com.reactive.inventory.module.inventory.domain.Inventory
import com.reactive.inventory.module.inventory.domain.InventoryCreate

interface IInventoryService {
    suspend fun tryGetByProductId(productId: Long): Inventory?
    suspend fun create(inventoryCreate: InventoryCreate): Inventory
    suspend fun update(inventory: Inventory): Inventory

    suspend fun getByProductId(productId: Long): Inventory {
        return tryGetByProductId(productId) ?: throw NotFoundException("Inventory with productId '$productId' does not exist")
    }
}
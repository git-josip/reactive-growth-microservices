package com.reactive.inventory.module.inventory.repository

import com.reactive.inventory.database.jooq.tables.records.InventoryRecord
import org.jooq.Configuration
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IInventoryJooqRepository {
    suspend fun insert(inventory: InventoryRecord, config: Configuration): InventoryRecord
    suspend fun update(inventory: InventoryRecord, config: Configuration): InventoryRecord
    suspend fun findByProductId(productId: Long, config: Configuration): InventoryRecord?
}
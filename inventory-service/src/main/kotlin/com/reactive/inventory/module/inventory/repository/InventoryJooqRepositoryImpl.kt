package com.reactive.inventory.module.inventory.repository

import com.reactive.inventory.common.configuration.DispatchersConfiguration
import com.reactive.inventory.database.jooq.tables.records.InventoryRecord
import com.reactive.inventory.database.jooq.tables.references.INVENTORY
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.jooq.Configuration
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class InventoryJooqRepositoryImpl: IInventoryJooqRepository {
    override suspend fun insert(inventory: InventoryRecord, config: Configuration): InventoryRecord {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)
            val sql = dslContext.insertInto(
                INVENTORY,
                INVENTORY.PRODUCT_ID,
                INVENTORY.QUANTITY
            ).values(
                inventory.productId,
                inventory.quantity
            ).returning()

            Mono
                .from(sql)
                .awaitSingle()
        }

    }

    override suspend fun update(inventory: InventoryRecord, config: Configuration): InventoryRecord {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)
            val sql = dslContext.update(INVENTORY)
                .set(INVENTORY.QUANTITY, inventory.quantity)
                .where(INVENTORY.PRODUCT_ID.eq(inventory.productId))
                .returning()

            Mono
                .from(sql)
                .awaitSingle()
        }
    }

    override suspend fun findByProductId(productId: Long, config: Configuration): InventoryRecord? {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)

            val sql = dslContext
                .select(INVENTORY.asterisk())
                .from(INVENTORY)
                .where(INVENTORY.PRODUCT_ID.eq(productId))

            Mono
                .from(sql)
                .mapNotNull { r -> r.into(INVENTORY)}
                .awaitSingleOrNull()
        }
    }
}
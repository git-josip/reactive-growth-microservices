package com.reactive.inventory.module.inventory.service

import com.reactive.inventory.common.jooq.DslContextTransactionAware
import com.reactive.inventory.module.inventory.domain.Inventory
import com.reactive.inventory.module.inventory.domain.InventoryCreate
import com.reactive.inventory.module.inventory.event.kafka.KafkaProducerService
import com.reactive.inventory.module.inventory.mapper.toInventory
import com.reactive.inventory.module.inventory.mapper.toInventoryRecord
import com.reactive.inventory.module.inventory.repository.IInventoryJooqRepository
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.kotlin.coroutines.transactionCoroutine
import org.springframework.stereotype.Service

@Service
class InventoryServiceImpl(
    private val inventoryJooqRepository: IInventoryJooqRepository,
    private val kafkaProducerService: KafkaProducerService,
    override val dslContext: DSLContext
): IInventoryService, DslContextTransactionAware {
    override suspend fun tryGetByProductId(productId: Long): Inventory? {
        return dslContext.transactionCoroutine { config: Configuration ->
            inventoryJooqRepository
                .findByProductId(productId, config)
                ?.toInventory()
        }
    }

    override suspend fun create(inventoryCreate: InventoryCreate): Inventory {
        return transactional { config: Configuration ->
            inventoryJooqRepository
                .insert(inventoryCreate.toInventoryRecord(), config)
                .toInventory()
        }
    }

    override suspend fun update(inventory: Inventory): Inventory {
        return transactional { config: Configuration ->
            inventoryJooqRepository
                .update(inventory.toInventoryRecord(), config)
                .toInventory()
        }
    }
}
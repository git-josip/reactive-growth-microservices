package com.reactive.order.module.order.repository

import com.reactive.order.common.configuration.DispatchersConfiguration
import com.reactive.order.database.jooq.tables.records.OrdersRecord
import com.reactive.order.database.jooq.tables.references.ORDERS
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.jooq.Configuration
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@Repository
class OrderJooqRepositoryImpl: IOrderJooqRepository {
    override suspend fun insert(order: OrdersRecord, config: Configuration): OrdersRecord {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)
            val sql = dslContext.insertInto(
                ORDERS,
                ORDERS.PRODUCT_ID,
                ORDERS.QUANTITY,
                ORDERS.PRICE,
                ORDERS.STATUS
            ).values(
                order.productId,
                order.quantity,
                order.price,
                order.status
            ).returning()

            Mono
                .from(sql)
                .awaitSingle()
        }
    }

    override suspend fun updateStatus(order: OrdersRecord, config: Configuration): OrdersRecord {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)
            val sql = dslContext.update(ORDERS)
                .set(ORDERS.STATUS, order.status)
                .set(ORDERS.DETAILS, order.details)
                .where(ORDERS.ID.eq(order.id))
                .returning()

            Mono
                .from(sql)
                .awaitSingle()
        }
    }

    override suspend fun findById(id: Long, config: Configuration): OrdersRecord? {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)

            val sql = dslContext
                .select(ORDERS.asterisk())
                .from(ORDERS)
                .where(ORDERS.ID.eq(id))

            Mono
                .from(sql)
                .mapNotNull { r -> r.into(ORDERS)}
                .awaitSingleOrNull()
        }
    }

    override suspend fun findByProductId(productId: Long, config: Configuration): List<OrdersRecord> {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)

            val sql = dslContext
                .select(ORDERS.asterisk())
                .from(ORDERS)
                .where(ORDERS.PRODUCT_ID.eq(productId))

            Flux.from(sql)
                .map { r -> r.into(ORDERS) }
                .toFlux()
                .asFlow()
                .toList()
        }
    }
}
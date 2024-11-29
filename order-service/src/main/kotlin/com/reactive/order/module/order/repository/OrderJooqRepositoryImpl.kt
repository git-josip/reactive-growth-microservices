package com.reactive.order.module.order.repository

import com.reactive.order.common.configuration.DispatchersConfiguration
import com.reactive.order.database.jooq.tables.records.OrdersRecord
import com.reactive.order.database.jooq.tables.references.ORDERS
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.jooq.Configuration
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class OrderJooqRepositoryImpl: IOrderJooqRepository {
    override suspend fun insert(order: OrdersRecord, config: Configuration): OrdersRecord {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)
            val sql = dslContext.insertInto(
                ORDERS,
                ORDERS.ID,
                ORDERS.PRODUCT_ID,
                ORDERS.QUANTITY,
                ORDERS.PRICE
            ).values(
                order.id,
                order.productId,
                order.quantity,
                order.price
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
}
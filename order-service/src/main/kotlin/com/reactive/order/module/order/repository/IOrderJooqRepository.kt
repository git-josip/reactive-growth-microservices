package com.reactive.order.module.order.repository

import com.reactive.order.database.jooq.tables.records.OrdersRecord
import org.jooq.Configuration
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IOrderJooqRepository {
    suspend fun insert(order: OrdersRecord, config: Configuration): OrdersRecord
    suspend fun updateStatus(order: OrdersRecord, config: Configuration): OrdersRecord
    suspend fun findById(id: Long, config: Configuration): OrdersRecord?
    suspend fun findByProductId(productId: Long, config: Configuration): List<OrdersRecord>
}
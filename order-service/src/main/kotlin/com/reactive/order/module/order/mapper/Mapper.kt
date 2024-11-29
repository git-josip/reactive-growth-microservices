package com.reactive.order.module.order.mapper

import com.reactive.order.database.jooq.tables.records.OrdersRecord
import com.reactive.order.module.order.domain.Order
import com.reactive.order.module.order.domain.OrderCreate
import com.reactive.order.module.order.event.domain.OrderEvent

fun OrdersRecord.toOrder(): Order {
    return Order(
        id = this.id!!,
        productId = this.productId,
        quantity = this.quantity,
        price = this.price,
        status = this.status,
        details = this.details,
        createdAt = this.createdAt!!,
        updatedAt = this.updatedAt!!
    )
}

fun Order.toOrdersRecord(): OrdersRecord {
    return OrdersRecord(
        id = this.id,
        productId = this.productId,
        quantity = this.quantity,
        price = this.price,
        status = this.status,
        details = this.details,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}


fun Order.toOrderCreatedEvent(): OrderEvent {
    return OrderEvent(
        orderId = this.id,
        type = "ORDER_CREATED",
        productId = this.productId,
        quantity = this.quantity,
        price = this.price,
        status = this.status
    )
}

fun Order.toOrderUpdatedEvent(): OrderEvent {
    return OrderEvent(
        orderId = this.id,
        type = "ORDER_UPDATED",
        productId = this.productId,
        quantity = this.quantity,
        price = this.price,
        status = this.status,
        details = this.details
    )
}

fun OrderCreate.toOrdersRecord(): OrdersRecord {
    return OrdersRecord(
        productId = this.productId,
        quantity = this.quantity,
        price = this.price,
        status = "PROCESSING",
    )
}
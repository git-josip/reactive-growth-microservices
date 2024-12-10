package com.reactive.order.module.order.mapper

import com.reactive.order.database.jooq.tables.records.OrdersRecord
import com.reactive.order.module.order.domain.Order
import com.reactive.order.module.order.domain.OrderCreate
import com.reactive.order.module.order.event.domain.OrderEvent
import java.math.BigDecimal
import java.time.ZoneOffset

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

fun OrderEvent.toOrderCreate(): OrderCreate {
    return OrderCreate(
        productId = this.productId,
        quantity = this.quantity,
        price = this.price
    )
}

fun OrderEvent.toOrdersRecord(): OrdersRecord {
    return OrdersRecord(
        productId = this.productId,
        quantity = this.quantity,
        price = this.price,
        status = "PROCESSING",
    )
}


fun com.reactive.order.module.order.grpc.dto.CreateOrderRequest.toDomainOrderCreate(): OrderCreate {
    return OrderCreate(
        productId = this.productId,
        quantity = this.quantity,
        price = BigDecimal(this.price)
    )
}

fun Order.toGrpcOrderResponse(): com.reactive.order.module.order.grpc.dto.OrderResponse {
    return com.reactive.order.module.order.grpc.dto.OrderResponse.newBuilder()
        .setId(this.id)
        .setProductId(this.productId)
        .setQuantity(this.quantity)
        .setPrice(this.price.toString())
        .setStatus(this.status)
        .setDetails(this.details ?: "")
        .setCreatedAt(
            com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(this.createdAt.toEpochSecond(ZoneOffset.UTC))
                .setNanos(this.createdAt.nano)
                .build()
        )
        .setUpdatedAt(
            com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(this.updatedAt.toEpochSecond(ZoneOffset.UTC))
                .setNanos(this.updatedAt.nano)
                .build()
        )
        .build()
}
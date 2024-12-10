package com.reactive.order.module.order.service

import com.reactive.order.common.exception.NotFoundException
import com.reactive.order.module.order.domain.Order
import com.reactive.order.module.order.domain.OrderCreate

interface IOrderService {
    suspend fun tryGetById(id: Long): Order?
    suspend fun create(orderCreate: OrderCreate): Order
    suspend fun update(order: Order): Order
    suspend fun orderCreated(order: Order)
    suspend fun orderUpdated(order: Order)
    suspend fun getByProductId(productId: Long): List<Order>

    suspend fun getById(id: Long): Order {
        return tryGetById(id) ?: throw NotFoundException("Order with id '$id' does not exist")
    }
}
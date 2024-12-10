package com.reactive.order.module.order.service

import com.reactive.order.common.configuration.ObjectMapperConfiguration
import com.reactive.order.common.jooq.DslContextTransactionAware
import com.reactive.order.module.order.domain.Order
import com.reactive.order.module.order.domain.OrderCreate
import com.reactive.order.module.order.event.kafka.KafkaProducerService
import com.reactive.order.module.order.mapper.*
import com.reactive.order.module.order.repository.IOrderJooqRepository
import com.reactive.order.module.order.validation.OrderCreateValidator
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.kotlin.coroutines.transactionCoroutine
import org.springframework.cloud.sleuth.annotation.NewSpan
import org.springframework.stereotype.Service

@Service
class OrderServiceImpl(
    private val orderJooqRepository: IOrderJooqRepository,
    private val orderCreateValidator: OrderCreateValidator,
    private val kafkaProducerService: KafkaProducerService,
    override val dslContext: DSLContext
): IOrderService, DslContextTransactionAware {
    override suspend fun tryGetById(id: Long): Order? {
        return dslContext.transactionCoroutine { config: Configuration ->
            orderJooqRepository
                .findById(id, config)
                ?.toOrder()
        }
    }

    @NewSpan("OrderService.create")
    override suspend fun create(orderCreate: OrderCreate): Order {
        return transactional { config: Configuration ->
            orderCreateValidator.validate(orderCreate, config).failOnError()

            val order = orderJooqRepository
                .insert(orderCreate.toOrdersRecord(), config)
                .toOrder()

            this.orderCreated(order)

            order
        }
    }

    override suspend fun update(order: Order): Order {
        return transactional { config: Configuration ->
            orderJooqRepository
                .updateStatus(order.toOrdersRecord(), config)
                .toOrder()
        }
    }

    override suspend fun orderCreated(order: Order) {
        val orderCreatedEvent = order.toOrderCreatedEvent()
        kafkaProducerService.sendMessages(
            "orders",
            orderCreatedEvent.orderId.toString(),
            ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(orderCreatedEvent),
        )
    }

    @NewSpan("OrderService.orderUpdated")
    override suspend fun orderUpdated(order: Order) {
        val orderUpdatedEvent = order.toOrderUpdatedEvent()
        kafkaProducerService.sendMessages(
            "orders",
            orderUpdatedEvent.orderId.toString(),
            ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(orderUpdatedEvent),
        )
    }

    override suspend fun getByProductId(productId: Long): List<Order> {
        return transactional { config: Configuration ->
            orderJooqRepository
                .findByProductId(productId, config)
                .map { it.toOrder() }
        }
    }
}
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

    override suspend fun create(orderCreate: OrderCreate): Order {
        return transactional { config: Configuration ->
            orderCreateValidator.validate(orderCreate, config).failOnError()

            orderJooqRepository
                .insert(orderCreate.toOrdersRecord(), config)
                .toOrder()
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

    override suspend fun orderUpdated(order: Order) {
        val orderCreatedEvent = order.toOrderUpdatedEvent()
        kafkaProducerService.sendMessages(
            "orders",
            orderCreatedEvent.orderId.toString(),
            ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(orderCreatedEvent),
        )
    }
}
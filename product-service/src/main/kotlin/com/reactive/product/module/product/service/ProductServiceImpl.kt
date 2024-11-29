package com.reactive.product.module.product.service

import com.reactive.product.common.configuration.ObjectMapperConfiguration
import com.reactive.product.common.jooq.DslContextTransactionAware
import com.reactive.product.module.product.domain.OrderCreate
import com.reactive.product.module.product.domain.Product
import com.reactive.product.module.product.domain.ProductCreate
import com.reactive.product.module.product.event.kafka.KafkaProducerService
import com.reactive.product.module.product.mapper.toInitOrderEvent
import com.reactive.product.module.product.mapper.toProduct
import com.reactive.product.module.product.mapper.toProductCreatedEvent
import com.reactive.product.module.product.mapper.toProductsRecord
import com.reactive.product.module.product.repository.IProductJooqRepository
import com.reactive.product.module.product.validation.OrderCreateValidator
import com.reactive.product.module.product.validation.ProductCreateValidator
import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl(
    private val productRepository: IProductJooqRepository,
    private val productCreateValidator: ProductCreateValidator,
    private val orderCreateValidator: OrderCreateValidator,
    private val kafkaProducerService: KafkaProducerService,
    override val dslContext: DSLContext
) : IProductService, DslContextTransactionAware {
    override suspend fun create(productCreate: ProductCreate): Product {
        return transactional { config: Configuration ->
            productCreateValidator.validate(productCreate, config).failOnError()

            val productCreated = productRepository
                .insert(productCreate.toProductsRecord(), config)
                .toProduct()

            kafkaProducerService.sendMessages(
                "products",
                productCreated.id.toString(),
                ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(productCreated.toProductCreatedEvent(productCreate.quantity))
            )

            productCreated
        }
    }

    override suspend fun createOrder(orderCreate: OrderCreate) {
        transactional { config: Configuration ->
            orderCreateValidator.validate(orderCreate, config).failOnError()

            kafkaProducerService.sendMessages(
                "orders",
                orderCreate.productId.toString(),
                ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(orderCreate.toInitOrderEvent())
            )
        }
    }

    override suspend fun tryGetById(id: Long): Product? {
        return transactional { config: Configuration ->
            productRepository
                .findById(id, config)
                ?.toProduct()
        }
    }

    override suspend fun count(): Int {
        return transactional { config: Configuration ->
            productRepository
                .count(config)
        }
    }

    override suspend fun findAll(pageable: Pageable): Page<Product> {
        return transactional { config: Configuration ->
            productRepository
                .findAll(pageable, config)
                .map { it.toProduct() }
        }
    }
}
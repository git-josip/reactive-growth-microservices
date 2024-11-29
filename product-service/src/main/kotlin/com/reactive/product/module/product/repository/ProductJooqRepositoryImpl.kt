package com.reactive.product.module.product.repository

import com.reactive.product.common.configuration.DispatchersConfiguration
import com.reactive.product.database.jooq.tables.records.ProductsRecord
import com.reactive.product.database.jooq.tables.references.PRODUCTS
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.jooq.Configuration
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class ProductJooqRepositoryImpl: IProductJooqRepository {
    override suspend fun findById(id: Long, config: Configuration): ProductsRecord? {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val sql = DSL.using(config)
                .select(PRODUCTS.asterisk())
                .from(PRODUCTS)
                .where(PRODUCTS.ID.eq(id))

            Mono.from(sql)
                .mapNotNull { r -> r.into(PRODUCTS)}
                .awaitSingleOrNull()
        }
    }

    override suspend fun findByName(name: String, config: Configuration): ProductsRecord? {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val sql = DSL.using(config)
                .select(PRODUCTS.asterisk())
                .from(PRODUCTS)
                .where(PRODUCTS.NAME.eq(name))

            Mono.from(sql)
                .mapNotNull { r -> r.into(PRODUCTS)}
                .awaitSingleOrNull()
        }
    }

    override suspend fun insert(product: ProductsRecord, config: Configuration): ProductsRecord {
        val sql = DSL.using(config).insertInto(
            PRODUCTS,
            PRODUCTS.NAME,
            PRODUCTS.CATEGORY,
            PRODUCTS.PRICE,
        ).values(
            product.name.lowercase(),
            product.category.uppercase(),
            product.price
        ).returning()

        return Mono.from(sql)
            .awaitSingle()
    }

    override suspend fun count(config: Configuration): Int {
        val dslContext = DSL.using(config)
        val sql = dslContext
            .selectCount()
            .from(PRODUCTS)

        return Mono
            .from(sql)
            .map { it.into(Int::class.java) }
            .awaitSingle()
    }

    override suspend fun findAll(pageable: Pageable, config: Configuration): Page<ProductsRecord> {
        val dslContext = DSL.using(config)
        val sql = dslContext
            .select(PRODUCTS.asterisk())
            .from(PRODUCTS)
            .orderBy(PRODUCTS.ID.desc())
            .limit(pageable.pageSize)
            .offset(pageable.offset.toInt())

        val countQuery = dslContext
            .selectCount()
            .from(PRODUCTS)

        val dataFlux = Flux.from(sql)
            .map { r -> r.into(PRODUCTS) }

        val countMono = Mono.from(countQuery)
            .map { it.into(Int::class.java) }

        return Mono.zip(dataFlux.collectList(), countMono)
            .map { tuple -> PageImpl(tuple.t1, pageable, tuple.t2.toLong()) }
            .awaitSingle()
    }
}
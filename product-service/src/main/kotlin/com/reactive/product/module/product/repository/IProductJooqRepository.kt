package com.reactive.product.module.product.repository

import com.reactive.product.database.jooq.tables.records.ProductsRecord
import org.jooq.Configuration
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IProductJooqRepository {
    suspend fun insert(product: ProductsRecord, config: Configuration): ProductsRecord
    suspend fun findById(id: Long, config: Configuration): ProductsRecord?
    suspend fun findByName(name: String, config: Configuration): ProductsRecord?
    suspend fun count(config: Configuration): Int
    suspend fun findAll(pageable: Pageable, config: Configuration): Page<ProductsRecord>
}
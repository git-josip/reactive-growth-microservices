package com.reactive.product.module.transaction.repository

import com.reactive.product.database.jooq.tables.records.TransactionsRecord
import org.jooq.Configuration
import org.springframework.data.repository.NoRepositoryBean
import java.util.UUID

@NoRepositoryBean
interface ITransactionJooqRepository {
    suspend fun insert(transaction: TransactionsRecord, config: Configuration): TransactionsRecord
    suspend fun findById(id: UUID, config: Configuration): TransactionsRecord?
}
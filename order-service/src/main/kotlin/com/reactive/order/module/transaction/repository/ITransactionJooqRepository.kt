package com.reactive.order.module.transaction.repository

import com.reactive.order.database.jooq.tables.records.TransactionsRecord
import org.jooq.Configuration
import org.springframework.data.repository.NoRepositoryBean
import java.util.UUID

@NoRepositoryBean
interface ITransactionJooqRepository {
    suspend fun insert(transaction: TransactionsRecord, config: Configuration): TransactionsRecord
    suspend fun findById(id: UUID, config: Configuration): TransactionsRecord?
}
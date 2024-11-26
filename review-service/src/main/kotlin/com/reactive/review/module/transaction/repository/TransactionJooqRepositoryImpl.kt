package com.reactive.review.module.transaction.repository

import com.reactive.review.common.configuration.DispatchersConfiguration
import com.reactive.review.database.jooq.tables.records.TransactionsRecord
import com.reactive.review.database.jooq.tables.references.TRANSACTIONS
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.jooq.Configuration
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class TransactionJooqRepositoryImpl: ITransactionJooqRepository {
    override suspend fun insert(transaction: TransactionsRecord, config: Configuration): TransactionsRecord {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)
            val sql = dslContext.insertInto(
                TRANSACTIONS,
                TRANSACTIONS.ID,
                TRANSACTIONS.AMOUNT,
                TRANSACTIONS.FROM_ACC,
                TRANSACTIONS.TO_ACC,
                TRANSACTIONS.CREATED_AT
            ).values(
                transaction.id,
                transaction.amount,
                transaction.fromAcc,
                transaction.toAcc,
                transaction.createdAt
            ).returning()

            Mono
                .from(sql)
                .awaitSingle()
        }

    }

    override suspend fun findById(id: UUID, config: Configuration): TransactionsRecord? {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)

            val sql = dslContext
                .select(TRANSACTIONS.asterisk())
                .from(TRANSACTIONS)
                .where(TRANSACTIONS.ID.eq(id))

            Mono
                .from(sql)
                .mapNotNull { r -> r.into(TRANSACTIONS)}
                .awaitSingleOrNull()
        }
    }
}
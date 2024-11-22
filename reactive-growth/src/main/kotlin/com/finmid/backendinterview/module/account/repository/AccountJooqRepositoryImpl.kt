package com.finmid.backendinterview.module.account.repository

import com.finmid.backendinterview.common.configuration.DispatchersConfiguration
import com.finmid.backendinterview.database.jooq.tables.records.AccountsRecord
import com.finmid.backendinterview.database.jooq.tables.references.ACCOUNTS
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
class AccountJooqRepositoryImpl: IAccountJooqRepository {
    override suspend fun findById(id: String, config: Configuration): AccountsRecord? {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val sql = DSL.using(config)
                .select(ACCOUNTS.asterisk())
                .from(ACCOUNTS)
                .where(ACCOUNTS.ID.eq(id))

            Mono.from(sql)
                .mapNotNull { r -> r.into(ACCOUNTS)}
                .awaitSingleOrNull()
        }
    }

    override suspend fun insert(account: AccountsRecord, config: Configuration): AccountsRecord {
        val sql = DSL.using(config).insertInto(
            ACCOUNTS,
            ACCOUNTS.ID,
            ACCOUNTS.BALANCE,
            ACCOUNTS.VERSION
        ).values(
            account.id,
            account.balance,
            account.version
        ).returning()

        return Mono.from(sql)
            .awaitSingle()
    }

    override suspend fun updateBalanceForVersion(account: AccountsRecord, config: Configuration): Int {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)
            val sql = dslContext
                .update(ACCOUNTS)
                .set(ACCOUNTS.BALANCE, account.balance)
                .set(ACCOUNTS.VERSION, ACCOUNTS.VERSION + 1)
                .where(ACCOUNTS.ID.eq(account.id).and(ACCOUNTS.VERSION.eq(account.version)))

            Mono.from(sql)
                .awaitSingle()
        }
    }

    override suspend fun updateBalance(account: AccountsRecord, config: Configuration): Int {
        return withContext(DispatchersConfiguration.DATABASE_DISPATCHERS) {
            val dslContext = DSL.using(config)
            val sql = dslContext
                .update(ACCOUNTS)
                .set(ACCOUNTS.BALANCE, account.balance)
                .where(ACCOUNTS.ID.eq(account.id))

            Mono.from(sql)
                .awaitSingle()
        }
    }

    override suspend fun count(config: Configuration): Int {
        val dslContext = DSL.using(config)
        val sql = dslContext
            .selectCount()
            .from(ACCOUNTS)

        return Mono
            .from(sql)
            .map { it.into(Int::class.java) }
            .awaitSingle()
    }

    override suspend fun findAll(pageable: Pageable, config: Configuration): Page<AccountsRecord> {
        val dslContext = DSL.using(config)
        val sql = dslContext
            .select(ACCOUNTS.asterisk())
            .from(ACCOUNTS)
            .orderBy(ACCOUNTS.BALANCE.desc())
            .limit(pageable.pageSize)
            .offset(pageable.offset.toInt())

        val countQuery = dslContext
            .selectCount()
            .from(ACCOUNTS)

        val dataFlux = Flux.from(sql)
            .map { r -> r.into(ACCOUNTS) }

        val countMono = Mono.from(countQuery)
            .map { it.into(Int::class.java) }

        return Mono.zip(dataFlux.collectList(), countMono)
            .map { tuple -> PageImpl(tuple.t1, pageable, tuple.t2.toLong()) }
            .awaitSingle()
    }
}
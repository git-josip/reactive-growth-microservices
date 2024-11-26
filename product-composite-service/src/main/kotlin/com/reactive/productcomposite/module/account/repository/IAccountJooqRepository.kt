package com.reactive.productcomposite.module.account.repository

import com.reactive.productcomposite.database.jooq.tables.records.AccountsRecord
import org.jooq.Configuration
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IAccountJooqRepository {
    suspend fun insert(account: AccountsRecord, config: Configuration): AccountsRecord
    suspend fun updateBalanceForVersion(account: AccountsRecord, config: Configuration): Int
    suspend fun updateBalance(account: AccountsRecord, config: Configuration): Int
    suspend fun findById(id: String, config: Configuration): AccountsRecord?
    suspend fun count(config: Configuration): Int
    suspend fun findAll(pageable: Pageable, config: Configuration): Page<AccountsRecord>
}
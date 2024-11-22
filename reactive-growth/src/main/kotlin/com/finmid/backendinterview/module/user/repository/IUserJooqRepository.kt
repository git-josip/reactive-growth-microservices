package com.finmid.backendinterview.module.user.repository

import com.finmid.backendinterview.database.jooq.tables.records.UsersRecord
import org.jooq.Configuration
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IUserJooqRepository {
    suspend fun insert(user: UsersRecord, config: Configuration): UsersRecord
    suspend fun findById(id: Long, config: Configuration): UsersRecord?
    suspend fun findByUsername(username: String, config: Configuration): UsersRecord?
}
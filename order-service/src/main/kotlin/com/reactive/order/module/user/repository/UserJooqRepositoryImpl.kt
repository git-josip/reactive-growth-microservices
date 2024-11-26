package com.reactive.order.module.user.repository

import com.reactive.order.database.jooq.tables.records.UsersRecord
import com.reactive.order.database.jooq.tables.references.USERS
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.jooq.Configuration
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class UserJooqRepositoryImpl: com.reactive.order.module.user.repository.IUserJooqRepository {
    override suspend fun insert(user: UsersRecord, config: Configuration): UsersRecord {
        val dslContext = DSL.using(config)
        val sql = dslContext.insertInto(
            USERS,
            USERS.USERNAME,
            USERS.PASSWORD
        ).values(
            user.username,
            user.password
        ).returning()

        return Mono.from(sql)
            .map { it.into(USERS) }
            .awaitSingle()
    }

    override suspend fun findById(id: Long, config: Configuration): UsersRecord? {
        val dslContext = DSL.using(config)
        val sql = dslContext
            .select(USERS.asterisk())
            .from(USERS)
            .where(USERS.ID.eq(id))

        return Mono.from(sql)
            .mapNotNull { r -> r.into(USERS)}
            .awaitSingleOrNull()
    }

    override suspend fun findByUsername(username: String, config: Configuration): UsersRecord? {
        val dslContext = DSL.using(config)
        val sql = dslContext
            .select(USERS.asterisk())
            .from(USERS)
            .where(USERS.USERNAME.eq(username))

        return Mono.from(sql)
            .mapNotNull { r -> r.into(USERS)}
            .awaitSingleOrNull()
    }
}
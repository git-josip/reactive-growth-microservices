/*
 * This file is generated by jOOQ.
 */
package com.reactive.productcomposite.database.jooq.indexes


import com.reactive.productcomposite.database.jooq.tables.FlywaySchemaHistory
import com.reactive.productcomposite.database.jooq.tables.Transactions
import com.reactive.productcomposite.database.jooq.tables.Users

import org.jooq.Index
import org.jooq.impl.DSL
import org.jooq.impl.Internal



// -------------------------------------------------------------------------
// INDEX definitions
// -------------------------------------------------------------------------

val FLYWAY_SCHEMA_HISTORY_S_IDX: Index = Internal.createIndex(DSL.name("flyway_schema_history_s_idx"), FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, arrayOf(FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.SUCCESS), false)
val IDX_TRANSACTION_CREATED_AT: Index = Internal.createIndex(DSL.name("idx_transaction_created_at"), Transactions.TRANSACTIONS, arrayOf(Transactions.TRANSACTIONS.CREATED_AT), false)
val IDX_USERS_CREATED_AT: Index = Internal.createIndex(DSL.name("idx_users_created_at"), Users.USERS, arrayOf(Users.USERS.CREATED_AT), false)
val IDX_USERS_USERNAME: Index = Internal.createIndex(DSL.name("idx_users_username"), Users.USERS, arrayOf(Users.USERS.USERNAME), true)
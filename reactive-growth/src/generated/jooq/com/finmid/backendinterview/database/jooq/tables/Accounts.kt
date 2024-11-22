/*
 * This file is generated by jOOQ.
 */
package com.finmid.backendinterview.database.jooq.tables


import com.finmid.backendinterview.database.jooq.Public
import com.finmid.backendinterview.database.jooq.keys.ACCOUNTS_PKEY
import com.finmid.backendinterview.database.jooq.keys.TRANSACTIONS__TRANSACTIONS_FROM_ACC_FKEY
import com.finmid.backendinterview.database.jooq.keys.TRANSACTIONS__TRANSACTIONS_TO_ACC_FKEY
import com.finmid.backendinterview.database.jooq.tables.Transactions.TransactionsPath
import com.finmid.backendinterview.database.jooq.tables.records.AccountsRecord

import java.math.BigDecimal

import kotlin.collections.Collection
import kotlin.collections.List

import org.jooq.Check
import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.InverseForeignKey
import org.jooq.Name
import org.jooq.Path
import org.jooq.PlainSQL
import org.jooq.QueryPart
import org.jooq.Record
import org.jooq.SQL
import org.jooq.Schema
import org.jooq.Select
import org.jooq.Stringly
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class Accounts(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, AccountsRecord>?,
    parentPath: InverseForeignKey<out Record, AccountsRecord>?,
    aliased: Table<AccountsRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<AccountsRecord>(
    alias,
    Public.PUBLIC,
    path,
    childPath,
    parentPath,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table(),
    where,
) {
    companion object {

        /**
         * The reference instance of <code>public.accounts</code>
         */
        val ACCOUNTS: Accounts = Accounts()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<AccountsRecord> = AccountsRecord::class.java

    /**
     * The column <code>public.accounts.id</code>.
     */
    val ID: TableField<AccountsRecord, String?> = createField(DSL.name("id"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>public.accounts.balance</code>.
     */
    val BALANCE: TableField<AccountsRecord, BigDecimal?> = createField(DSL.name("balance"), SQLDataType.NUMERIC(15, 2).nullable(false), this, "")

    /**
     * The column <code>public.accounts.version</code>.
     */
    val VERSION: TableField<AccountsRecord, Long?> = createField(DSL.name("version"), SQLDataType.BIGINT.nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<AccountsRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<AccountsRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<AccountsRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>public.accounts</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>public.accounts</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>public.accounts</code> table reference
     */
    constructor(): this(DSL.name("accounts"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, AccountsRecord>?, parentPath: InverseForeignKey<out Record, AccountsRecord>?): this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, ACCOUNTS, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class AccountsPath : Accounts, Path<AccountsRecord> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, AccountsRecord>?, parentPath: InverseForeignKey<out Record, AccountsRecord>?): super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<AccountsRecord>): super(alias, aliased)
        override fun `as`(alias: String): AccountsPath = AccountsPath(DSL.name(alias), this)
        override fun `as`(alias: Name): AccountsPath = AccountsPath(alias, this)
        override fun `as`(alias: Table<*>): AccountsPath = AccountsPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Public.PUBLIC
    override fun getPrimaryKey(): UniqueKey<AccountsRecord> = ACCOUNTS_PKEY

    private lateinit var _transactionsFromAccFkey: TransactionsPath

    /**
     * Get the implicit to-many join path to the
     * <code>public.transactions</code> table, via the
     * <code>transactions_from_acc_fkey</code> key
     */
    fun transactionsFromAccFkey(): TransactionsPath {
        if (!this::_transactionsFromAccFkey.isInitialized)
            _transactionsFromAccFkey = TransactionsPath(this, null, TRANSACTIONS__TRANSACTIONS_FROM_ACC_FKEY.inverseKey)

        return _transactionsFromAccFkey;
    }

    val transactionsFromAccFkey: TransactionsPath
        get(): TransactionsPath = transactionsFromAccFkey()

    private lateinit var _transactionsToAccFkey: TransactionsPath

    /**
     * Get the implicit to-many join path to the
     * <code>public.transactions</code> table, via the
     * <code>transactions_to_acc_fkey</code> key
     */
    fun transactionsToAccFkey(): TransactionsPath {
        if (!this::_transactionsToAccFkey.isInitialized)
            _transactionsToAccFkey = TransactionsPath(this, null, TRANSACTIONS__TRANSACTIONS_TO_ACC_FKEY.inverseKey)

        return _transactionsToAccFkey;
    }

    val transactionsToAccFkey: TransactionsPath
        get(): TransactionsPath = transactionsToAccFkey()
    override fun getChecks(): List<Check<AccountsRecord>> = listOf(
        Internal.createCheck(this, DSL.name("accounts_balance_check"), "((balance >= (0)::numeric))", true),
        Internal.createCheck(this, DSL.name("accounts_id_check"), "(((id)::text ~ '^[a-z0-9_-]+\$'::text))", true)
    )
    override fun `as`(alias: String): Accounts = Accounts(DSL.name(alias), this)
    override fun `as`(alias: Name): Accounts = Accounts(alias, this)
    override fun `as`(alias: Table<*>): Accounts = Accounts(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Accounts = Accounts(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Accounts = Accounts(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Accounts = Accounts(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Accounts = Accounts(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Accounts = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Accounts = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Accounts = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Accounts = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Accounts = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Accounts = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Accounts = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Accounts = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Accounts = where(DSL.notExists(select))
}

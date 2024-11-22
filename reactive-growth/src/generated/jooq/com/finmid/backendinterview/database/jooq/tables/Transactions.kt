/*
 * This file is generated by jOOQ.
 */
package com.finmid.backendinterview.database.jooq.tables


import com.finmid.backendinterview.database.jooq.Public
import com.finmid.backendinterview.database.jooq.indexes.IDX_TRANSACTION_CREATED_AT
import com.finmid.backendinterview.database.jooq.keys.TRANSACTIONS_PKEY
import com.finmid.backendinterview.database.jooq.keys.TRANSACTIONS__TRANSACTIONS_FROM_ACC_FKEY
import com.finmid.backendinterview.database.jooq.keys.TRANSACTIONS__TRANSACTIONS_TO_ACC_FKEY
import com.finmid.backendinterview.database.jooq.tables.Accounts.AccountsPath
import com.finmid.backendinterview.database.jooq.tables.records.TransactionsRecord

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

import kotlin.collections.Collection
import kotlin.collections.List

import org.jooq.Check
import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Index
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
open class Transactions(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, TransactionsRecord>?,
    parentPath: InverseForeignKey<out Record, TransactionsRecord>?,
    aliased: Table<TransactionsRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<TransactionsRecord>(
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
         * The reference instance of <code>public.transactions</code>
         */
        val TRANSACTIONS: Transactions = Transactions()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<TransactionsRecord> = TransactionsRecord::class.java

    /**
     * The column <code>public.transactions.id</code>.
     */
    val ID: TableField<TransactionsRecord, UUID?> = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "")

    /**
     * The column <code>public.transactions.amount</code>.
     */
    val AMOUNT: TableField<TransactionsRecord, BigDecimal?> = createField(DSL.name("amount"), SQLDataType.NUMERIC(15, 2).nullable(false), this, "")

    /**
     * The column <code>public.transactions.from_acc</code>.
     */
    val FROM_ACC: TableField<TransactionsRecord, String?> = createField(DSL.name("from_acc"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>public.transactions.to_acc</code>.
     */
    val TO_ACC: TableField<TransactionsRecord, String?> = createField(DSL.name("to_acc"), SQLDataType.VARCHAR(40).nullable(false), this, "")

    /**
     * The column <code>public.transactions.created_at</code>.
     */
    val CREATED_AT: TableField<TransactionsRecord, LocalDateTime?> = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP"), SQLDataType.LOCALDATETIME)), this, "")

    private constructor(alias: Name, aliased: Table<TransactionsRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<TransactionsRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<TransactionsRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>public.transactions</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>public.transactions</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>public.transactions</code> table reference
     */
    constructor(): this(DSL.name("transactions"), null)

    constructor(path: Table<out Record>, childPath: ForeignKey<out Record, TransactionsRecord>?, parentPath: InverseForeignKey<out Record, TransactionsRecord>?): this(Internal.createPathAlias(path, childPath, parentPath), path, childPath, parentPath, TRANSACTIONS, null, null)

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    open class TransactionsPath : Transactions, Path<TransactionsRecord> {
        constructor(path: Table<out Record>, childPath: ForeignKey<out Record, TransactionsRecord>?, parentPath: InverseForeignKey<out Record, TransactionsRecord>?): super(path, childPath, parentPath)
        private constructor(alias: Name, aliased: Table<TransactionsRecord>): super(alias, aliased)
        override fun `as`(alias: String): TransactionsPath = TransactionsPath(DSL.name(alias), this)
        override fun `as`(alias: Name): TransactionsPath = TransactionsPath(alias, this)
        override fun `as`(alias: Table<*>): TransactionsPath = TransactionsPath(alias.qualifiedName, this)
    }
    override fun getSchema(): Schema? = if (aliased()) null else Public.PUBLIC
    override fun getIndexes(): List<Index> = listOf(IDX_TRANSACTION_CREATED_AT)
    override fun getPrimaryKey(): UniqueKey<TransactionsRecord> = TRANSACTIONS_PKEY
    override fun getReferences(): List<ForeignKey<TransactionsRecord, *>> = listOf(TRANSACTIONS__TRANSACTIONS_FROM_ACC_FKEY, TRANSACTIONS__TRANSACTIONS_TO_ACC_FKEY)

    private lateinit var _transactionsFromAccFkey: AccountsPath

    /**
     * Get the implicit join path to the <code>public.accounts</code> table, via
     * the <code>transactions_from_acc_fkey</code> key.
     */
    fun transactionsFromAccFkey(): AccountsPath {
        if (!this::_transactionsFromAccFkey.isInitialized)
            _transactionsFromAccFkey = AccountsPath(this, TRANSACTIONS__TRANSACTIONS_FROM_ACC_FKEY, null)

        return _transactionsFromAccFkey;
    }

    val transactionsFromAccFkey: AccountsPath
        get(): AccountsPath = transactionsFromAccFkey()

    private lateinit var _transactionsToAccFkey: AccountsPath

    /**
     * Get the implicit join path to the <code>public.accounts</code> table, via
     * the <code>transactions_to_acc_fkey</code> key.
     */
    fun transactionsToAccFkey(): AccountsPath {
        if (!this::_transactionsToAccFkey.isInitialized)
            _transactionsToAccFkey = AccountsPath(this, TRANSACTIONS__TRANSACTIONS_TO_ACC_FKEY, null)

        return _transactionsToAccFkey;
    }

    val transactionsToAccFkey: AccountsPath
        get(): AccountsPath = transactionsToAccFkey()
    override fun getChecks(): List<Check<TransactionsRecord>> = listOf(
        Internal.createCheck(this, DSL.name("transactions_amount_check"), "((amount > (0)::numeric))", true)
    )
    override fun `as`(alias: String): Transactions = Transactions(DSL.name(alias), this)
    override fun `as`(alias: Name): Transactions = Transactions(alias, this)
    override fun `as`(alias: Table<*>): Transactions = Transactions(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Transactions = Transactions(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Transactions = Transactions(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Transactions = Transactions(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Transactions = Transactions(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Transactions = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Transactions = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Transactions = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Transactions = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Transactions = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Transactions = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Transactions = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Transactions = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Transactions = where(DSL.notExists(select))
}

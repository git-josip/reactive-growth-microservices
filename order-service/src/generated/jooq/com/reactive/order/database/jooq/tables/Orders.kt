/*
 * This file is generated by jOOQ.
 */
package com.reactive.order.database.jooq.tables


import com.reactive.order.database.jooq.Public
import com.reactive.order.database.jooq.keys.ORDERS_PKEY
import com.reactive.order.database.jooq.tables.records.OrdersRecord

import java.math.BigDecimal
import java.time.LocalDateTime

import kotlin.collections.Collection

import org.jooq.Condition
import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Identity
import org.jooq.InverseForeignKey
import org.jooq.Name
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
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class Orders(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, OrdersRecord>?,
    parentPath: InverseForeignKey<out Record, OrdersRecord>?,
    aliased: Table<OrdersRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<OrdersRecord>(
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
         * The reference instance of <code>public.orders</code>
         */
        val ORDERS: Orders = Orders()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<OrdersRecord> = OrdersRecord::class.java

    /**
     * The column <code>public.orders.id</code>.
     */
    val ID: TableField<OrdersRecord, Long?> = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "")

    /**
     * The column <code>public.orders.product_id</code>.
     */
    val PRODUCT_ID: TableField<OrdersRecord, Long?> = createField(DSL.name("product_id"), SQLDataType.BIGINT.nullable(false), this, "")

    /**
     * The column <code>public.orders.quantity</code>.
     */
    val QUANTITY: TableField<OrdersRecord, Int?> = createField(DSL.name("quantity"), SQLDataType.INTEGER.nullable(false), this, "")

    /**
     * The column <code>public.orders.price</code>.
     */
    val PRICE: TableField<OrdersRecord, BigDecimal?> = createField(DSL.name("price"), SQLDataType.NUMERIC(15, 2).nullable(false), this, "")

    /**
     * The column <code>public.orders.status</code>.
     */
    val STATUS: TableField<OrdersRecord, String?> = createField(DSL.name("status"), SQLDataType.VARCHAR(50).nullable(false), this, "")

    /**
     * The column <code>public.orders.details</code>.
     */
    val DETAILS: TableField<OrdersRecord, String?> = createField(DSL.name("details"), SQLDataType.VARCHAR(500), this, "")

    /**
     * The column <code>public.orders.created_at</code>.
     */
    val CREATED_AT: TableField<OrdersRecord, LocalDateTime?> = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP"), SQLDataType.LOCALDATETIME)), this, "")

    /**
     * The column <code>public.orders.updated_at</code>.
     */
    val UPDATED_AT: TableField<OrdersRecord, LocalDateTime?> = createField(DSL.name("updated_at"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP"), SQLDataType.LOCALDATETIME)), this, "")

    private constructor(alias: Name, aliased: Table<OrdersRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<OrdersRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<OrdersRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>public.orders</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>public.orders</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>public.orders</code> table reference
     */
    constructor(): this(DSL.name("orders"), null)
    override fun getSchema(): Schema? = if (aliased()) null else Public.PUBLIC
    override fun getIdentity(): Identity<OrdersRecord, Long?> = super.getIdentity() as Identity<OrdersRecord, Long?>
    override fun getPrimaryKey(): UniqueKey<OrdersRecord> = ORDERS_PKEY
    override fun `as`(alias: String): Orders = Orders(DSL.name(alias), this)
    override fun `as`(alias: Name): Orders = Orders(alias, this)
    override fun `as`(alias: Table<*>): Orders = Orders(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Orders = Orders(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Orders = Orders(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Orders = Orders(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Orders = Orders(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Orders = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Orders = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Orders = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Orders = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Orders = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Orders = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Orders = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Orders = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Orders = where(DSL.notExists(select))
}
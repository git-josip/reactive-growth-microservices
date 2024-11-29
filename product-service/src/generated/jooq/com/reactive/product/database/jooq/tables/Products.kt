/*
 * This file is generated by jOOQ.
 */
package com.reactive.product.database.jooq.tables


import com.reactive.product.database.jooq.Public
import com.reactive.product.database.jooq.keys.PRODUCTS_PKEY
import com.reactive.product.database.jooq.tables.records.ProductsRecord

import java.math.BigDecimal

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
open class Products(
    alias: Name,
    path: Table<out Record>?,
    childPath: ForeignKey<out Record, ProductsRecord>?,
    parentPath: InverseForeignKey<out Record, ProductsRecord>?,
    aliased: Table<ProductsRecord>?,
    parameters: Array<Field<*>?>?,
    where: Condition?
): TableImpl<ProductsRecord>(
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
         * The reference instance of <code>public.products</code>
         */
        val PRODUCTS: Products = Products()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<ProductsRecord> = ProductsRecord::class.java

    /**
     * The column <code>public.products.id</code>.
     */
    val ID: TableField<ProductsRecord, Long?> = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "")

    /**
     * The column <code>public.products.name</code>.
     */
    val NAME: TableField<ProductsRecord, String?> = createField(DSL.name("name"), SQLDataType.VARCHAR(255).nullable(false), this, "")

    /**
     * The column <code>public.products.category</code>.
     */
    val CATEGORY: TableField<ProductsRecord, String?> = createField(DSL.name("category"), SQLDataType.VARCHAR(255).nullable(false), this, "")

    /**
     * The column <code>public.products.price</code>.
     */
    val PRICE: TableField<ProductsRecord, BigDecimal?> = createField(DSL.name("price"), SQLDataType.NUMERIC(15, 2).nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<ProductsRecord>?): this(alias, null, null, null, aliased, null, null)
    private constructor(alias: Name, aliased: Table<ProductsRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, null, aliased, parameters, null)
    private constructor(alias: Name, aliased: Table<ProductsRecord>?, where: Condition?): this(alias, null, null, null, aliased, null, where)

    /**
     * Create an aliased <code>public.products</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>public.products</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>public.products</code> table reference
     */
    constructor(): this(DSL.name("products"), null)
    override fun getSchema(): Schema? = if (aliased()) null else Public.PUBLIC
    override fun getIdentity(): Identity<ProductsRecord, Long?> = super.getIdentity() as Identity<ProductsRecord, Long?>
    override fun getPrimaryKey(): UniqueKey<ProductsRecord> = PRODUCTS_PKEY
    override fun `as`(alias: String): Products = Products(DSL.name(alias), this)
    override fun `as`(alias: Name): Products = Products(alias, this)
    override fun `as`(alias: Table<*>): Products = Products(alias.qualifiedName, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Products = Products(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Products = Products(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Products = Products(name.qualifiedName, null)

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Condition?): Products = Products(qualifiedName, if (aliased()) this else null, condition)

    /**
     * Create an inline derived table from this table
     */
    override fun where(conditions: Collection<Condition>): Products = where(DSL.and(conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(vararg conditions: Condition?): Products = where(DSL.and(*conditions))

    /**
     * Create an inline derived table from this table
     */
    override fun where(condition: Field<Boolean?>?): Products = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(condition: SQL): Products = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String): Products = where(DSL.condition(condition))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg binds: Any?): Products = where(DSL.condition(condition, *binds))

    /**
     * Create an inline derived table from this table
     */
    @PlainSQL override fun where(@Stringly.SQL condition: String, vararg parts: QueryPart): Products = where(DSL.condition(condition, *parts))

    /**
     * Create an inline derived table from this table
     */
    override fun whereExists(select: Select<*>): Products = where(DSL.exists(select))

    /**
     * Create an inline derived table from this table
     */
    override fun whereNotExists(select: Select<*>): Products = where(DSL.notExists(select))
}

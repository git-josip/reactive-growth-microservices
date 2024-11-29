/*
 * This file is generated by jOOQ.
 */
package com.reactive.inventory.database.jooq


import com.reactive.inventory.database.jooq.tables.FlywaySchemaHistory
import com.reactive.inventory.database.jooq.tables.Inventory

import kotlin.collections.List

import org.jooq.Catalog
import org.jooq.Table
import org.jooq.impl.SchemaImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class Public : SchemaImpl("public", DefaultCatalog.DEFAULT_CATALOG) {
    public companion object {

        /**
         * The reference instance of <code>public</code>
         */
        val PUBLIC: Public = Public()
    }

    /**
     * The table <code>public.flyway_schema_history</code>.
     */
    val FLYWAY_SCHEMA_HISTORY: FlywaySchemaHistory get() = FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY

    /**
     * The table <code>public.inventory</code>.
     */
    val INVENTORY: Inventory get() = Inventory.INVENTORY

    override fun getCatalog(): Catalog = DefaultCatalog.DEFAULT_CATALOG

    override fun getTables(): List<Table<*>> = listOf(
        FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY,
        Inventory.INVENTORY
    )
}

/*
 * This file is generated by jOOQ.
 */
package com.reactive.order.database.jooq.tables.records


import com.reactive.order.database.jooq.tables.FlywaySchemaHistory

import java.time.LocalDateTime

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class FlywaySchemaHistoryRecord private constructor() : UpdatableRecordImpl<FlywaySchemaHistoryRecord>(FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY) {

    open var installedRank: Int
        set(value): Unit = set(0, value)
        get(): Int = get(0) as Int

    open var version: String?
        set(value): Unit = set(1, value)
        get(): String? = get(1) as String?

    open var description: String
        set(value): Unit = set(2, value)
        get(): String = get(2) as String

    open var type: String
        set(value): Unit = set(3, value)
        get(): String = get(3) as String

    open var script: String
        set(value): Unit = set(4, value)
        get(): String = get(4) as String

    open var checksum: Int?
        set(value): Unit = set(5, value)
        get(): Int? = get(5) as Int?

    open var installedBy: String
        set(value): Unit = set(6, value)
        get(): String = get(6) as String

    open var installedOn: LocalDateTime?
        set(value): Unit = set(7, value)
        get(): LocalDateTime? = get(7) as LocalDateTime?

    open var executionTime: Int
        set(value): Unit = set(8, value)
        get(): Int = get(8) as Int

    open var success: Boolean
        set(value): Unit = set(9, value)
        get(): Boolean = get(9) as Boolean

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Int?> = super.key() as Record1<Int?>

    /**
     * Create a detached, initialised FlywaySchemaHistoryRecord
     */
    constructor(installedRank: Int, version: String? = null, description: String, type: String, script: String, checksum: Int? = null, installedBy: String, installedOn: LocalDateTime? = null, executionTime: Int, success: Boolean): this() {
        this.installedRank = installedRank
        this.version = version
        this.description = description
        this.type = type
        this.script = script
        this.checksum = checksum
        this.installedBy = installedBy
        this.installedOn = installedOn
        this.executionTime = executionTime
        this.success = success
        resetChangedOnNotNull()
    }
}

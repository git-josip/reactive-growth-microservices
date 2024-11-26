/*
 * This file is generated by jOOQ.
 */
package com.reactive.inventory.database.jooq.tables.records


import com.reactive.inventory.database.jooq.tables.Accounts

import java.math.BigDecimal

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class AccountsRecord private constructor() : UpdatableRecordImpl<AccountsRecord>(Accounts.ACCOUNTS) {

    open var id: String
        set(value): Unit = set(0, value)
        get(): String = get(0) as String

    open var balance: BigDecimal
        set(value): Unit = set(1, value)
        get(): BigDecimal = get(1) as BigDecimal

    open var version: Long
        set(value): Unit = set(2, value)
        get(): Long = get(2) as Long

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<String?> = super.key() as Record1<String?>

    /**
     * Create a detached, initialised AccountsRecord
     */
    constructor(id: String, balance: BigDecimal, version: Long): this() {
        this.id = id
        this.balance = balance
        this.version = version
        resetChangedOnNotNull()
    }
}

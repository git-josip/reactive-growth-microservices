/*
 * This file is generated by jOOQ.
 */
package com.reactive.order.database.jooq.tables.records


import com.reactive.order.database.jooq.tables.Orders

import java.math.BigDecimal
import java.time.LocalDateTime

import org.jooq.Record1
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class OrdersRecord private constructor() : UpdatableRecordImpl<OrdersRecord>(Orders.ORDERS) {

    open var id: Long?
        set(value): Unit = set(0, value)
        get(): Long? = get(0) as Long?

    open var productId: Long
        set(value): Unit = set(1, value)
        get(): Long = get(1) as Long

    open var quantity: Int
        set(value): Unit = set(2, value)
        get(): Int = get(2) as Int

    open var price: BigDecimal
        set(value): Unit = set(3, value)
        get(): BigDecimal = get(3) as BigDecimal

    open var status: String
        set(value): Unit = set(4, value)
        get(): String = get(4) as String

    open var details: String?
        set(value): Unit = set(5, value)
        get(): String? = get(5) as String?

    open var createdAt: LocalDateTime?
        set(value): Unit = set(6, value)
        get(): LocalDateTime? = get(6) as LocalDateTime?

    open var updatedAt: LocalDateTime?
        set(value): Unit = set(7, value)
        get(): LocalDateTime? = get(7) as LocalDateTime?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Long?> = super.key() as Record1<Long?>

    /**
     * Create a detached, initialised OrdersRecord
     */
    constructor(id: Long? = null, productId: Long, quantity: Int, price: BigDecimal, status: String, details: String? = null, createdAt: LocalDateTime? = null, updatedAt: LocalDateTime? = null): this() {
        this.id = id
        this.productId = productId
        this.quantity = quantity
        this.price = price
        this.status = status
        this.details = details
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        resetChangedOnNotNull()
    }
}

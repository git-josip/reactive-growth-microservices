package com.reactive.order.module.order.event.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.reactive.order.common.events.domain.BaseMutexEvent
import java.math.BigDecimal

data class OrderEvent(
    val orderId: Long,
    val type: String,
    val status: String,
    val productId: Long,
    val quantity: Int,
    val price: BigDecimal,
    val details: String? = null
): BaseMutexEvent {
    @get:JsonIgnore
    override val mutexKey: String
        get() = orderId.toString()
}
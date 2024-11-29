package com.reactive.order.module.order.event.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.reactive.order.common.events.domain.BaseMutexEvent

data class InventoryValidatedEvent(
    val orderId: Long,
    val status: String,
    val details: String
): BaseMutexEvent {
    @get:JsonIgnore
    override val mutexKey: String
        get() = orderId.toString()
}
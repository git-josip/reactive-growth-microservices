package com.reactive.order.common.events.subscriber

interface IEvenSubService<T> {
    suspend fun handleEvent(event: T)
}
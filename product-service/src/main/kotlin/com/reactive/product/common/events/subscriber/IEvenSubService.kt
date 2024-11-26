package com.reactive.product.common.events.subscriber

interface IEvenSubService<T> {
    suspend fun handleEvent(event: T)
}
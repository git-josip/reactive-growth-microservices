package com.reactive.inventory.common.events.subscriber

interface IEvenSubService<T> {
    suspend fun handleEvent(event: T)
}
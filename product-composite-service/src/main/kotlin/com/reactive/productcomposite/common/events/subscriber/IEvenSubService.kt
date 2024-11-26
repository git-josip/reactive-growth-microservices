package com.reactive.productcomposite.common.events.subscriber

interface IEvenSubService<T> {
    suspend fun handleEvent(event: T)
}
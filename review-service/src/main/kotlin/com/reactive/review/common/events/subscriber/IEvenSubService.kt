package com.reactive.review.common.events.subscriber

interface IEvenSubService<T> {
    suspend fun handleEvent(event: T)
}
package com.reactive.recommendation.common.events.subscriber

interface IEvenSubService<T> {
    suspend fun handleEvent(event: T)
}
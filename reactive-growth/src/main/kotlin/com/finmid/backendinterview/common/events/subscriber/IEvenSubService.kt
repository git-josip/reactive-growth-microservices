package com.finmid.backendinterview.common.events.subscriber

interface IEvenSubService<T> {
    suspend fun handleEvent(event: T)
}
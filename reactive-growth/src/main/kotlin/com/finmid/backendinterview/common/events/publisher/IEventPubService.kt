package com.finmid.backendinterview.common.events.publisher

import kotlinx.coroutines.flow.SharedFlow

interface IEventPubService<T> {
    val events: SharedFlow<T>
    suspend fun publishEvent(event: T)
}
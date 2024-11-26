package com.reactive.inventory.common.events.subscriber

import com.reactive.inventory.common.events.domain.BaseMutexEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class EvenSubService<T: BaseMutexEvent>(
    events: SharedFlow<T>,
    flowBuffer: Int = 1000
): com.reactive.inventory.common.events.subscriber.IEvenSubService<T> {
    private val accountMutexMap = mutableMapOf<String, Mutex>()

    init {
        events
            .buffer(flowBuffer)
            .onEach { event ->
                val fromAccountMutex = accountMutexMap.computeIfAbsent(event.mutexKey) { Mutex() }
                fromAccountMutex.withLock {
                    handleEvent(event)
                }
            }
            .launchIn(CoroutineScope(Dispatchers.Default))
    }

    abstract override suspend fun handleEvent(event: T)
}